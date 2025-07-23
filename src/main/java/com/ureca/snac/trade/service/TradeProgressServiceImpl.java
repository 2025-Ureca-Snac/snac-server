package com.ureca.snac.trade.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeInvalidStatusException;
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
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member seller = tradeSupport.findMember(username);
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

    @Override
    @Transactional
    public Long confirmTrade(Long tradeId, String username) {
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member buyer = tradeSupport.findMember(username);
//        Wallet wallet = tradeSupport.findLockedWallet(trade.getSeller().getId());
        Card card = tradeSupport.findLockedCard(trade.getCardId());
        Member seller = trade.getSeller();

        trade.confirm(buyer); // 거래 상태를 확정으로 변경
        card.changeSellStatus(SOLD_OUT); // 카드 상태를 판매 완료로 변경

        long amountToDeposit = trade.getPriceGb();
        long finalBalance = walletService.depositMoney(seller.getId(),
                amountToDeposit);

        String title = String.format("%s %dGB 판매 대금", card.getCarrier().name(),
                card.getDataAmount());
        AssetChangedEvent event = assetChangedEventFactory.createForSell(
                seller.getId(), trade.getId(), title,
                amountToDeposit, finalBalance
        );

        assetHistoryEventPublisher.publish(event);

        return trade.getId();
    }
}
