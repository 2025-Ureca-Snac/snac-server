package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardInvalidStatusException;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.*;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.ureca.snac.board.entity.constants.SellStatus.*;
import static com.ureca.snac.trade.entity.TradeStatus.DATA_SENT;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Slf4j
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
        return buildTrade(createTradeRequest, username, SELLING);
    }

    @Override
    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        return buildTrade(createTradeRequest, username, PENDING);
    }

    @Override
    @Transactional
    public void cancelTrade(Long tradeId, String username) {
        Trade trade = findLockedTrade(tradeId);
        Member member = findMember(username);
        Wallet wallet = findLockedWallet(trade.getBuyer().getId());
        Card card = findLockedCard(trade.getCardId());

        trade.cancel(member);

        int refundMoney = trade.getPriceGb() - trade.getPoint();
        if (refundMoney > 0) wallet.depositMoney(refundMoney);
        if (trade.getPoint() > 0) wallet.depositPoint(trade.getPoint());

        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public void sendTradeData(Long tradeId, String username, MultipartFile picture) {
        log.info("file Name : {}", picture.getOriginalFilename());

        Trade trade = findLockedTrade(tradeId);
        Member seller = findMember(username);
        Card card = findLockedCard(trade.getCardId());

        if (trade.getStatus() != PAYMENT_CONFIRMED) {
            throw new TradeInvalidStatusException();
        }

        if (trade.getBuyer() == seller)
            throw new TradeSendPermissionDeniedException();

        if (trade.getSeller() == null) { // 구매글의 경우 판매자가 정해지지 않았기 때문에 지정
            trade.changeSeller(seller);
            card.changeSellStatus(TRADING);

        } else if (trade.getSeller() != seller) {
            throw new TradeSendPermissionDeniedException();
        }

        trade.changeStatus(DATA_SENT);
    }

    @Override
    @Transactional
    public void confirmTrade(Long tradeId, String username) {
        Trade trade = findLockedTrade(tradeId);
        Member buyer = findMember(username);
        Wallet wallet = findLockedWallet(trade.getSeller().getId());
        Card card = findLockedCard(trade.getCardId());

        trade.confirm(buyer);
        card.changeSellStatus(SOLD_OUT);
        wallet.depositMoney(trade.getPriceGb() - trade.getPoint());
    }

    public ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, Long lastTradeId) {
        Member member = findMember(username);

        List<Trade> trades = (side == TradeSide.BUY)
                ? tradeRepository.findTradesByBuyerInfinite(member.getId(), lastTradeId, size + 1)
                : tradeRepository.findTradesBySellerInfinite(member.getId(), lastTradeId, size + 1);

        boolean hasNext = trades.size() > size;

        List<TradeResponse> dto = trades.stream()
                .limit(size)
                .map(t -> TradeResponse.from(t, side))
                .toList();

        return new ScrollTradeResponse(dto, hasNext);
    }

    // === private helper === //
    private Long buildTrade(CreateTradeRequest createTradeRequest, String username, SellStatus requiredStatus) {
        Member member = findMember(username);
        Wallet wallet = findLockedWallet(member.getId());
        Card card = findLockedCard(createTradeRequest.getCardId());

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
        card.changeSellStatus(requiredStatus == SELLING ? TRADING : SELLING);

        return trade.getId();
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private Wallet findLockedWallet(Long memberId) {
        return walletRepository.findByMemberIdWithLock(memberId).orElseThrow(WalletNotFoundException::new);
    }

    private Card findLockedCard(Long cardId) {
        return cardRepository.findLockedById(cardId).orElseThrow(CardNotFoundException::new);
    }

    private Trade findLockedTrade(Long tradeId) {
        return tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
    }

    private void ensureStatus(Card card, SellStatus sellStatus) {
        if (card.getSellStatus() != sellStatus) {
            throw new CardInvalidStatusException();
        }
    }

    private void ensureOwnership(Card card, Member member, SellStatus requiredStatus) {
        boolean isOwner = card.getMember().equals(member);

        if (requiredStatus == SELLING && isOwner) {
            throw new TradeSelfRequestException();
        }
        if (requiredStatus == PENDING && !isOwner) {
            throw new TradePermissionDeniedException();
        }
    }
}
