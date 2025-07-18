package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.board.exception.CardInvalidStatusException;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.exception.TradePaymentMismatchException;
import com.ureca.snac.trade.exception.TradePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeSelfRequestException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeInitiationService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.board.entity.constants.SellStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeInitiationServiceImpl implements TradeInitiationService {
    private final TradeRepository tradeRepository;
    private final TradeSupport tradeSupport;

    @Override
    @Transactional
    public Long createSellTrade(CreateTradeRequest createTradeRequest, String username) {
        return buildTrade(createTradeRequest, username, SELLING);
    }

    @Override
    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        return buildTrade(createTradeRequest, username, PENDING);
    }

    @Override
    @Transactional
    public Long acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username) {
        Card card = tradeSupport.findLockedCard(claimBuyRequest.getCardId());
        Member seller = tradeSupport.findMember(username);
        Trade trade = tradeRepository
                .findLockedByCardId(claimBuyRequest.getCardId())
                .orElseThrow(TradeNotFoundException::new);

        if (card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        if (trade.getBuyer() == seller) {
            throw new TradeSelfRequestException();
        }

        trade.changeSeller(seller);
        card.changeSellStatus(TRADING);

        return trade.getId();
    }

    // === private helper === //
    private Long buildTrade(CreateTradeRequest createTradeRequest, String username, SellStatus requiredStatus) {
        Member member = tradeSupport.findMember(username);
        Wallet wallet = tradeSupport.findLockedWallet(member.getId());
        Card card = tradeSupport.findLockedCard(createTradeRequest.getCardId());

        if (requiredStatus == SELLING && card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        ensureStatus(card, requiredStatus);
        ensureOwnership(card, member, requiredStatus);

        int totalPay = createTradeRequest.getMoney() + createTradeRequest.getPoint();

        if (card.getPrice() != totalPay)
            throw new TradePaymentMismatchException();

        if (createTradeRequest.getMoney() != 0) {
            wallet.withdrawMoney(createTradeRequest.getMoney());
        }
        if (createTradeRequest.getPoint() != 0) {
            wallet.withdrawPoint(createTradeRequest.getPoint());
        }

        Trade trade = Trade.buildTrade(createTradeRequest.getPoint(), member, member.getPhone(), card, requiredStatus);
        tradeRepository.save(trade);

        // 카드 상태 변경 (판매글이면 TRADING, 구매글이면 SELLING)
        card.changeSellStatus(requiredStatus == SELLING ? TRADING : SELLING);

        return trade.getId();
    }

    private void ensureStatus(Card card, SellStatus sellStatus) {
        if (card.getSellStatus() != sellStatus) {
            throw new CardInvalidStatusException();
        }
    }

    private void ensureOwnership(Card card, Member member, SellStatus requiredStatus) {
        boolean isOwner = card.getMember().equals(member);

        // 판매 요청 시 자기 글 요청 방지
        if (requiredStatus == SELLING && isOwner) {
            throw new TradeSelfRequestException();
        }
        // 구매 요청 시 타인의 글만 허용
        if (requiredStatus == PENDING && !isOwner) {
            throw new TradePermissionDeniedException();
        }
    }
}
