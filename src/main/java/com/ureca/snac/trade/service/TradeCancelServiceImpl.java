package com.ureca.snac.trade.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.controller.request.CancelBuyRequest;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.exception.*;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.trade.service.interfaces.TradeCancelService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ureca.snac.trade.entity.CancelReason.NOT_SELECTED;
import static com.ureca.snac.trade.entity.TradeStatus.BUY_REQUESTED;
import static com.ureca.snac.trade.entity.TradeStatus.CANCELED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TradeCancelServiceImpl implements TradeCancelService {

    private final TradeCancelRepository cancelRepo;
    private final CardRepository cardRepo;
    private final TradeRepository tradeRepo;
    private final TradeSupport tradeSupport;
    private final PenaltyService penaltyService;

    private final WalletService walletService;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    @Override
    public void requestCancel(Long tradeId, String userEmail, CancelReason reason) {

        Trade trade = tradeSupport.findLockedTrade(tradeId);

        // 제목 생성위해서 만들었음.. 리팩토링 시 변경 가능
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        Member requester = tradeSupport.findMember(userEmail);
        // 구매자 따로 뺏음
        Member buyer = trade.getBuyer();

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

            // 거래 취소 및 환불
            trade.cancel(requester);

            refundToBuyerAndPublishEvent(trade, card, buyer);
//            일단 프라비잇으로 환불 로직이랑 이벤트 호출하는거 헬퍼 메소드로 뺏다. 중복 로직이라서 이후의 리팩토링은 자유
//            refundToBuyerAndPublishEvent 활용 trade랑 card 있어야함 -> 제목 필요
//            나는 wallet 의존관계 주입받아서 쓸꺼 굳이 엔티티 계층 접근필요 X

//            Wallet buyerWallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
//            long refundMoney = (long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount();
//            if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
//            if (trade.getPoint() > 0)
//                buyerWallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());  // 구매자에게 사용한 포인트 환불

            // 패널티: SELLER_FAULT
            penaltyService.givePenalty(requester.getEmail(), PenaltyReason.SELLER_FAULT);

            // 알림 추가

        } else {
            // 구매자 => 취소
            TradeCancel cancel = TradeCancel.builder()
                    .trade(trade)
                    .requester(requester)
                    .reason(reason)
                    .status(CancelStatus.REQUESTED)
                    .build();

            cancelRepo.save(cancel);
            // 알림 등 호출
        }
    }

    @Override
    public void acceptCancel(Long tradeId, String username) {
        TradeCancel cancel = cancelRepo.findByTradeId(tradeId)
                .orElseThrow(TradeCancelNotFoundException::new);

        Trade trade = cancel.getTrade();

        // 제목 때문에 필요
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        Member seller = tradeSupport.findMember(username);
        Member buyer = trade.getBuyer();

        // 이거도 그냥 의존관계 주입 받음 굳이 외부 컴포넌트의 엔티티 접근을 유도안하고 DI
//        Wallet wallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());

        // 판매자 본인만 승인
        if (!trade.getSeller().equals(seller))
            throw new TradeCancelPermissionDeniedException();

        if (cancel.getStatus() != CancelStatus.REQUESTED)
            throw new TradeInvalidStatusException();

        // 취소 처리
        cancel.accept();
        trade.cancel(seller); // 상태변경까지


//        위와 마찬가지 이유
        refundToBuyerAndPublishEvent(trade, card, buyer);
//        // 환불 로직 (TradeProgressService.cancelTrade에 있던 부분 재활용)
//        long refundMoney = (long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount();
//        if (refundMoney > 0) wallet.depositMoney(refundMoney);
//        if (trade.getPoint() > 0)
//            wallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());  // 구매자에게 사용한 포인트 환불

        // 패널티 (귀책자: 구매자)
        penaltyService.givePenalty(cancel.getRequester().getEmail(), PenaltyReason.BUYER_FAULT);
    }

    private void refundToBuyerAndPublishEvent(Trade trade, Card card, Member buyer) {
        long moneyToRefund = trade.getPriceGb() - trade.getPoint();

        if (moneyToRefund > 0) {
            long moneyFinalBalance = walletService.depositMoney(buyer.getId(),
                    moneyToRefund);

            String title = String.format("%s %dGB 거래 취소",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForTradeCancelWithMoney(
                    buyer.getId(), trade.getId(), title,
                    moneyToRefund, moneyFinalBalance
            );
            assetHistoryEventPublisher.publish(event);
        }

        long pointToRefund = trade.getPoint();
        if (pointToRefund > 0) {
            long pointFinalBalance = walletService.depositPoint(
                    buyer.getId(), pointToRefund
            );

            String title = String.format("%s %dGB 포인트 환불",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event =
                    assetChangedEventFactory.createForTradeCancelWithPoint(
                            buyer.getId(), trade.getId(), title,
                            pointToRefund, pointFinalBalance
                    );
            assetHistoryEventPublisher.publish(event);
        }
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


    // 거래는 1대1 매칭이기 때문에 선택되지 않는 다른 거래는 모두 자동 취소 처리 됩니다.
    @Override
    @Transactional
    public List<TradeDto> cancelOtherTradesOfCard(Long cardId, Long acceptedTradeId) {
        List<Trade> waitingTrades = tradeRepo.findLockedByCardIdAndStatus(cardId, BUY_REQUESTED)
                .stream()
                .filter(t -> !t.getId().equals(acceptedTradeId))
                .toList();

        waitingTrades.forEach(t -> {
            t.changeStatus(CANCELED);
            t.changeCancelReason(NOT_SELECTED);
        });

        return waitingTrades.stream()
                .map(TradeDto::from)
                .toList();
    }

    // 판매자가 수락 상태 전에 판매글을 내리는 경우입니다.
    // 해당 판매자에게 거래 요청을 보낸 모든 구매자의 거래를 취소 처리합니다.
    // 취소는 구매자가 작성한 값으로 처리합니다. (CancelReason)
    @Override
    @Transactional
    public List<TradeDto> cancelBuyRequestBySellerOfCard(CancelBuyRequest request, String username) {
        List<Trade> waitingTrades = tradeRepo.findLockedByCardIdAndStatus(request.getCardId(), BUY_REQUESTED);

        cardRepo.deleteById(request.getCardId());

        waitingTrades.forEach(t -> {
            t.changeStatus(CANCELED);
            t.changeCancelReason(request.getReason());
        });

        return waitingTrades.stream()
                .map(TradeDto::from)
                .toList();
    }


    // 구매자가 구매 요청을 보낸 후 수락 단계를 넘어가기 전에 스스로 거래를 취소한 경우입니다.
    // 판매자에게 해당 요청은 취소되었다는 알림이 전송됩니다.
    @Override
    @Transactional
    public TradeDto cancelBuyRequestByBuyerOfCard(CancelBuyRequest request, String username) {
        Member member = tradeSupport.findMember(username);

        Trade trade = tradeRepo.findLockedByCardIdAndBuyer(request.getCardId(), member)
                .orElseThrow(TradeNotFoundException::new);

        trade.cancel(member);
        trade.changeCancelReason(request.getReason());

        return TradeDto.from(trade);
    }

    // ACCEPT, PAYMENT 단계의 취소를 처리하는 메서드입니다.
    @Override
    @Transactional
    public TradeDto cancelRealTimeTrade(Long tradeId, String username, CancelReason reason) {
        log.info("[거래취소] 실시간 거래 취소 요청 - tradeId: {}, username: {}, reason: {}", tradeId, username, reason);

        Member member = tradeSupport.findMember(username);
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        if (trade.getStatus() == TradeStatus.PAYMENT_CONFIRMED) {
            refundToBuyerAndPublishEvent(trade, card, trade.getBuyer());
        }

        // 거래 수락전이라면 카드를 삭제하면 안됨
        if (trade.getStatus() != BUY_REQUESTED) {
            cardRepo.deleteById(trade.getCardId());
        }

        trade.cancel(member);
        trade.changeCancelReason(reason);

        return TradeDto.from(trade);
    }

//    @Override
//    @Transactional
//    public TradeDto cancelAcceptedTradeByBuyer(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username) {
//        Member member = tradeSupport.findMember(username);
//        Trade trade = tradeSupport.findLockedTrade(cancelRealTimeTradeRequest.getTradeId());
//
//        cancelRepo.deleteById(trade.getCardId());
//        trade.cancel(member);
//        trade.changeCancelReason(BUYER_CHANGE_MIND);
//
//        return TradeDto.from(trade);
//    }
//
//    @Override
//    @Transactional
//    public TradeDto cancelAcceptedTradeBySeller(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username) {
//        Member member = tradeSupport.findMember(username);
//        Trade trade = tradeSupport.findLockedTrade(cancelRealTimeTradeRequest.getTradeId());
//
//        cancelRepo.deleteById(trade.getCardId());
//
//        trade.cancel(member);
//        trade.changeCancelReason(SELLER_CHANGE_MIND);
//
//        return TradeDto.from(trade);
//    }
}
