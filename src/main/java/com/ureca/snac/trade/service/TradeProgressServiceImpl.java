package com.ureca.snac.trade.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeSendPermissionDeniedException;
import com.ureca.snac.trade.exception.TradeStatusMismatchException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeProgressService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.board.entity.constants.SellStatus.SOLD_OUT;
import static com.ureca.snac.trade.entity.TradeStatus.DATA_SENT;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeProgressServiceImpl implements TradeProgressService {
    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;

    private final TradeSupport tradeSupport;

    private final WalletService walletService;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    @Override
    @Transactional
    public Long sendTradeData(Long tradeId, String username) {
        Member seller = tradeSupport.findMember(username);
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        if (trade.getStatus() != PAYMENT_CONFIRMED) { // 결제가 완료되지 않은 상태에서는 판매자가 데이터를 전송할 수 없음
            throw new TradeStatusMismatchException();
        }

        if (trade.getBuyer() == seller || trade.getSeller() != seller) { // 이미 지정된 판매자가 현재 요청자가 아닐 경우 권한 없음
            throw new TradeSendPermissionDeniedException();
        }

        trade.changeStatus(DATA_SENT);

        return trade.getId();
    }

    // 해당 확정 메서드는 현재 실시간 매칭과 일반 매칭에 사용합니다.
    // 마지막에 상대방이 나간 경우 카드가 삭제되는 문제가 발생하여 거래가 확정이 안되는 문제가 있어 파라미터 hasCard를 추가합니다.
    // 일반 매칭 hasCard == true, 실시간 매칭 == false
    @Override
    @Transactional
    public Long confirmTrade(Long tradeId, String username, Boolean hasCard) {
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member buyer = tradeSupport.findMember(username);
//        Wallet wallet = tradeSupport.findLockedWallet(trade.getSeller().getId());
        Member seller = trade.getSeller();

        trade.confirm(buyer); // 거래 상태를 확정으로 변경

        if (hasCard) {
            Card card = tradeSupport.findLockedCard(trade.getCardId());
            card.changeSellStatus(SOLD_OUT); // 카드 상태를 판매 완료로 변경
        } else {
            // 실시간 매칭에서는 거래가 끝난 경우 카드는 필요 없으므로 삭제
            cardRepository.deleteById(trade.getCardId());
        }

        long amountToDeposit = trade.getPriceGb();
        long finalBalance = walletService.depositMoney(seller.getId(),
                amountToDeposit);

        String title = String.format("%s %dGB 판매 대금", trade.getCarrier().name(),
                trade.getDataAmount());
        AssetChangedEvent event = assetChangedEventFactory.createForSell(
                seller.getId(), trade.getId(), title,
                amountToDeposit, finalBalance
        );

        assetHistoryEventPublisher.publish(event);

        return trade.getId();
    }

    // 선택받지 못한 트레이드 자동 취소
//    @Override
//    @Transactional
//    public List<TradeDto> cancelOtherTradesOfCard(Long cardId, Long acceptedTradeId) {
//        List<Trade> waitingTrades = tradeRepository.findLockedByCardIdAndStatus(cardId, BUY_REQUESTED)
//                .stream()
//                .filter(t -> !t.getId().equals(acceptedTradeId))
//                .toList();
//
//        waitingTrades.forEach(t -> {
//            t.changeStatus(CANCELED);
//            t.changeCancelReason(NOT_SELECTED);
//        });
//
//        return waitingTrades.stream()
//                .map(TradeDto::from)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public TradeDto cancelBuyRequestByBuyerOfCard(CancelBuyRequest request, String username) {
//        Member member = tradeSupport.findMember(username);
//
//        Trade trade = tradeRepository.findLockedByCardIdAndBuyer(request.getCardId(),  member)
//                .orElseThrow(TradeNotFoundException::new);
//
//        trade.cancel(member);
//        trade.changeCancelReason(BUYER_CHANGE_MIND);
//
//        return TradeDto.from(trade);
//    }
//
//    @Override
//    @Transactional
//    public List<TradeDto> cancelBuyRequestBySellerOfCard(CancelBuyRequest request, String username) {
//        List<Trade> waitingTrades = tradeRepository.findLockedByCardIdAndStatus(request.getCardId(), BUY_REQUESTED);
//
//        cardRepository.deleteById(request.getCardId());
//
//        waitingTrades.forEach(t -> {
//            t.changeStatus(CANCELED);
//            t.changeCancelReason(SELLER_CHANGE_MIND);
//        });
//
//        return waitingTrades.stream()
//                .map(TradeDto::from)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public TradeDto cancelAcceptedTradeByBuyer(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username) {
//        Member member = tradeSupport.findMember(username);
//        Trade trade = tradeSupport.findLockedTrade(cancelRealTimeTradeRequest.getTradeId());
//
//        cardRepository.deleteById(trade.getCardId());
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
//        cardRepository.deleteById(trade.getCardId());
//
//        trade.cancel(member);
//        trade.changeCancelReason(SELLER_CHANGE_MIND);
//
//        return TradeDto.from(trade);
//    }
//
//    @Override
//    @Transactional
//    public TradeDto cancelRealTimeTrade(Long tradeId, String username, CancelReason reason) {
//        Trade trade = tradeSupport.findLockedTrade(tradeId);
//        Member member = tradeSupport.findMember(username);
//
//        trade.cancel(member);
//        trade.changeCancelReason(reason);
//
//        return TradeDto.from(trade);
//    }
//
//    @Override
//    @Transactional
//    public TradeDto cancelRealTimeTradeWithRefund(Long tradeId, String username) {
//        Trade trade = tradeSupport.findLockedTrade(tradeId);
//        Member member = tradeSupport.findMember(username);
//
//        if (trade.getPriceGb() - trade.getPoint() > 0) {
//            walletService.depositMoney(trade.getBuyer().getId(), trade.getPriceGb() - trade.getPoint());
//        }
//
//        if (trade.getPoint() > 0) {
//            walletService.depositPoint(trade.getBuyer().getId(), trade.getPoint());
//        }
//
//        return TradeDto.from(trade);
//    }
}
