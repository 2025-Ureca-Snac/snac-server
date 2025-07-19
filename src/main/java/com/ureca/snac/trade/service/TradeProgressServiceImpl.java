package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeInvalidStatusException;
import com.ureca.snac.trade.exception.TradeSendPermissionDeniedException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeProgressService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
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

    @Override
    @Transactional
    public Long sendTradeData(Long tradeId, String username) {
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member seller = tradeSupport.findMember(username);
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        if (trade.getStatus() != PAYMENT_CONFIRMED) {
            throw new TradeInvalidStatusException();
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
        Wallet wallet = tradeSupport.findLockedWallet(trade.getSeller().getId());
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        trade.confirm(buyer);
        card.changeSellStatus(SOLD_OUT);
        wallet.depositMoney(trade.getPriceGb() - trade.getPoint());

        return trade.getId();
    }

    @Override
    @Transactional
    public Long cancelTrade(Long tradeId, String username) {
        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member member = tradeSupport.findMember(username);
        Wallet wallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
        Card card = tradeSupport.findLockedCard(trade.getCardId());

        trade.cancel(member);

        int refundMoney = trade.getPriceGb() - trade.getPoint();
        if (refundMoney > 0) wallet.depositMoney(refundMoney);
        if (trade.getPoint() > 0) wallet.depositPoint(trade.getPoint());


        return card.getId();
    }
}
