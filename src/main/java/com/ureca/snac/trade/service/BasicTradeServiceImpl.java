package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.exception.CardAlreadySellingException;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.entity.CancelReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.exception.TradePaymentMismatchException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.InsufficientBalanceException;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.board.entity.constants.SellStatus.*;
import static com.ureca.snac.trade.entity.TradeStatus.*;
import static com.ureca.snac.trade.entity.TradeStatus.CANCELED;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicTradeServiceImpl implements BasicTradeService {

    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public Long createSellTrade(CreateTradeRequest createTradeRequest, String username) {
        Member buyer = getMember(username);
        Wallet wallet = getLockedWallet(buyer);
        Card card = getLockedCard(createTradeRequest);

        if (card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        validate(createTradeRequest, wallet, card);
        payment(createTradeRequest, wallet);

        Trade trade = Trade.builder().cardId(card.getId())
                .seller(card.getMember())
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(PAYMENT_CONFIRMED)
                .phone(buyer.getPhone())
                .point(createTradeRequest.getPoint())
                .build();

        tradeRepository.save(trade);
        card.changeSellStatus(TRADING);

        return trade.getId();
    }

    @Override
    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        Member buyer = getMember(username);
        Wallet wallet = getLockedWallet(buyer);
        Card card = getLockedCard(createTradeRequest);

        if (card.getSellStatus() != PENDING) {
            throw new CardAlreadySellingException();
        }

        validate(createTradeRequest, wallet, card);
        payment(createTradeRequest, wallet);

        Trade trade = Trade.builder().cardId(card.getId())
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(PAYMENT_CONFIRMED)
                .phone(buyer.getPhone())
                .point(createTradeRequest.getPoint())
                .build();

        tradeRepository.save(trade);
        card.changeSellStatus(SELLING);

        return trade.getId();
    }

    @Override
    @Transactional
    public void cancelTrade(Long tradeId, String username) {
        Trade trade = tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
        Member requester = getMember(username);
        Wallet wallet = walletRepository.findByMemberIdWithLock(requester.getId()).orElseThrow(WalletNotFoundException::new);
        Card card = getLockedCard(trade.getCardId());

        if ((trade.getStatus() == DATA_SENT) || (trade.getStatus() == COMPLETED) || (trade.getStatus() == CANCELED)) {
            throw new RuntimeException("취소할 수 없는 상황입니다.");
        }

        if (!requester.equals(trade.getBuyer()) && !requester.equals(trade.getSeller())) {
            throw new RuntimeException("취소 권한이 없습니다.");
        }

        if (requester.equals(trade.getBuyer())) {
            trade.changeCancelReason(CancelReason.BUYER_REQUEST);
        } else {
            trade.changeCancelReason(CancelReason.SELLER_REQUEST);
        }

        trade.changeStatus(CANCELED);
        wallet.depositMoney(trade.getPriceGb() - trade.getPoint());
        wallet.depositPoint(trade.getPoint());

        if (card.getCardCategory() == CardCategory.SELL) {
            card.changeSellStatus(SELLING);

        } else if (card.getCardCategory() == CardCategory.BUY) {
            card.changeSellStatus(PENDING);
        }
    }

    private void payment(CreateTradeRequest createTradeRequest, Wallet wallet) {
        if (createTradeRequest.getMoney() != 0) {
            wallet.withdrawMoney(createTradeRequest.getMoney());
        }

        if (createTradeRequest.getPoint() != 0) {
            wallet.withdrawPoint(createTradeRequest.getPoint());
        }
    }

    private void validate(CreateTradeRequest createTradeRequest, Wallet wallet, Card card) {
        int totalPaymentMoney = createTradeRequest.getPoint() + createTradeRequest.getMoney();

        if (wallet.getMoney() - createTradeRequest.getMoney() < 0) {
            throw new InsufficientBalanceException();
        }

        if (card.getPrice() != totalPaymentMoney) {
            throw new TradePaymentMismatchException();
        }
    }

    private Card getLockedCard(CreateTradeRequest createTradeRequest) {
        return cardRepository.findLockedById(createTradeRequest.getCardId()).orElseThrow(CardNotFoundException::new);
    }

    private Card getLockedCard(Long cardId) {
        return cardRepository.findLockedById(cardId).orElseThrow(CardNotFoundException::new);
    }

    private Wallet getLockedWallet(Member buyer) {
        return walletRepository.findByMemberIdWithLock(buyer.getId()).orElseThrow(WalletNotFoundException::new);
    }

    private Member getMember(String username) {
        return memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
    }
}
