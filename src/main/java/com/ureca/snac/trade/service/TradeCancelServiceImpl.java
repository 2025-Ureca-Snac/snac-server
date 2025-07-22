package com.ureca.snac.trade.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.exception.TradeAlreadyCancelRequestedException;
import com.ureca.snac.trade.exception.TradeCancelNotFoundException;
import com.ureca.snac.trade.exception.TradeCancelPermissionDeniedException;
import com.ureca.snac.trade.exception.TradeInvalidStatusException;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.trade.service.interfaces.TradeCancelService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeCancelServiceImpl implements TradeCancelService {

    private final TradeCancelRepository cancelRepo;
    private final TradeRepository tradeRepo;
    private final TradeSupport tradeSupport;
    private final PenaltyService penaltyService;

    @Override
    public void requestCancel(Long tradeId, String userEmail, CancelReason reason) {

        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member requester = tradeSupport.findMember(userEmail);

        // DATA_SENT 전만 가능
        if (trade.getStatus() == TradeStatus.DATA_SENT || trade.getStatus() == TradeStatus.COMPLETED || trade.getStatus() == TradeStatus.CANCELED)
            throw new TradeInvalidStatusException();

        // 이미 요청이 있으면 중복 차단
        if (cancelRepo.findByTradeId(tradeId).isPresent())
            throw new TradeAlreadyCancelRequestedException();

        TradeCancel cancel = TradeCancel.builder()
                .trade(trade)
                .requester(requester)
                .reason(reason)
                .status(CancelStatus.REQUESTED)
                .build();

        cancelRepo.save(cancel);

        // 알림 등 호출
    }

    @Override
    public void acceptCancel(Long tradeId, String username) {
        TradeCancel cancel = cancelRepo.findByTradeId(tradeId)
                .orElseThrow(TradeCancelNotFoundException::new);

        Trade trade = cancel.getTrade();
        Member seller = tradeSupport.findMember(username);
        Wallet wallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());

        // 판매자 본인만 승인
        if (!trade.getSeller().equals(seller))
            throw new TradeCancelPermissionDeniedException();

        if (cancel.getStatus() != CancelStatus.REQUESTED)
            throw new TradeInvalidStatusException();

        // 취소 처리
        cancel.accept();
        trade.cancel(seller); //상태변경까지
        // 환불 로직 (TradeProgressService.cancelTrade에 있던 부분 재활용)
        long refundMoney = (long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount();
        if (refundMoney > 0) wallet.depositMoney(refundMoney);
        if (trade.getPoint() > 0) wallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());  // 구매자에게 사용한 포인트 환불

        // 패널티 (귀책자: 구매자)
        penaltyService.givePenalty(cancel.getRequester().getEmail(), PenaltyReason.BUYER_FAULT);
    }

    @Override
    public void rejectCancel(Long tradeId, String username) {
        TradeCancel cancel = cancelRepo.findByTradeId(tradeId)
                .orElseThrow(TradeCancelNotFoundException::new);

        Trade trade = cancel.getTrade();
        Member seller = tradeSupport.findMember(username);

        if (!trade.getSeller().equals(seller))
            throw new TradeCancelPermissionDeniedException();

        if (cancel.getStatus() != CancelStatus.REQUESTED)
            throw new TradeInvalidStatusException();

        cancel.reject();  // 거래 계속 진행
    }
}