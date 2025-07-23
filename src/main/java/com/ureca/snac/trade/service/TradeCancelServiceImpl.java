package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.SellStatus;
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

import java.time.LocalDateTime;

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
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        // DATA_SENT 전만 가능
        if (trade.getStatus() == TradeStatus.DATA_SENT || trade.getStatus() == TradeStatus.COMPLETED || trade.getStatus() == TradeStatus.CANCELED)
            throw new TradeInvalidStatusException();

        // 이미 요청이 있으면 중복 차단
        if (cancelRepo.findByTradeId(tradeId).isPresent())
            throw new TradeAlreadyCancelRequestedException();

        boolean isSeller = requester.equals(trade.getSeller());

        // 판매자 => 즉시 취소
        if (isSeller) {
            // 취소 엔티티 저장: ACCEPTED & resolvedAt
            TradeCancel cancel = TradeCancel.builder()
                    .trade(trade)
                    .requester(requester)
                    .reason(reason)
                    .status(CancelStatus.ACCEPTED)
                    .resolvedAt(LocalDateTime.now())
                    .build();
            cancelRepo.save(cancel);

            // 카드 상태 처리
            // 지금 판매자가 취소 요청 상태인데 판매글이면 삭제 처리 / 구매글이면 다시 구매중으로
            if(card.getCardCategory() == CardCategory.SELL){
                card.changeSellStatus(SellStatus.CANCEL);
            } else if (card.getCardCategory() == CardCategory.BUY){
                card.changeSellStatus(SellStatus.SELLING);
            }

            // 거래 취소 및 환불
            trade.cancel(requester);

            Wallet buyerWallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
            long refundMoney = (long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount();
            if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
            if (trade.getPoint() > 0) buyerWallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());  // 구매자에게 사용한 포인트 환불

            // 패널티: SELLER_FAULT
            penaltyService.givePenalty(requester.getEmail(), PenaltyReason.SELLER_FAULT);

            // 알림 추가
            return;
        }

        TradeCancel cancel = TradeCancel.builder()
                .trade(trade)
                .requester(requester)
                .reason(reason)
                .status(CancelStatus.REQUESTED)
                .build();

        cancelRepo.save(cancel);

        // 알림 등 호출
    }

    // 허락하는건 판매자, 즉 취소 요청이 구매자
    @Override
    public void acceptCancel(Long tradeId, String username) {
        TradeCancel cancel = cancelRepo.findByTradeId(tradeId)
                .orElseThrow(TradeCancelNotFoundException::new);

        Trade trade = cancel.getTrade();
        Member seller = tradeSupport.findMember(username);
        Wallet wallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        // 판매자 본인만 승인
        if (!trade.getSeller().equals(seller))
            throw new TradeCancelPermissionDeniedException();

        if (cancel.getStatus() != CancelStatus.REQUESTED)
            throw new TradeInvalidStatusException();

        // 카드 상태 처리
        // 지금 구매자가 취소 요청 상태인데 구매글이면 삭제 처리 / 판매글이면 다시 판매중으로
        if(card.getCardCategory() == CardCategory.BUY){
            card.changeSellStatus(SellStatus.CANCEL);
        } else if (card.getCardCategory() == CardCategory.SELL){
            card.changeSellStatus(SellStatus.SELLING);
        }

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