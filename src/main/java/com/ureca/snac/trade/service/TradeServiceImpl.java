package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.notification.dto.NotificationDTO;
import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.exception.*;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.notification.entity.NotificationType;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.controller.request.TradeRequest;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Long requestTrade(TradeRequest tradeRequest, String username, TradeSide tradeSide) {
        Card card = cardRepository.findLockedById(tradeRequest.getCardId()).orElseThrow(CardNotFoundException::new);
        Member me = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Member other = memberRepository.findByEmail(card.getMember().getEmail()).orElseThrow(MemberNotFoundException::new);

        if (card.getSellStatus() != SellStatus.SELLING)
            throw new CardAlreadyTradingException();

        if (card.getMember().equals(me))
            throw new TradeSelfRequestException();

        Member buyer, seller;
        TradeStatus initialStatus;

        switch (tradeSide) {
            case BUY:
                if (card.getCardCategory() != CardCategory.SELL) {
                    throw new IllegalArgumentException("판매글이 아닌 글에는 구매자가 요청할 수 없습니다.");
                }
                initialStatus = TradeStatus.BUY_REQUESTED;
                buyer  = me;
                seller = other;
                break;

            case SELL:
                if (card.getCardCategory() != CardCategory.BUY) {
                    throw new IllegalArgumentException("구매글이 아닌 글에는 판매자가 요청할 수 없습니다.");
                }
                initialStatus = TradeStatus.SELL_REQUESTED;
                seller = me;
                buyer  = other;
                break;

            default:
                throw new IllegalArgumentException("side는 BUY 또는 SELL만 가능합니다.");
        }

        Trade trade = Trade.builder().cardId(card.getId())
                .seller(seller)
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(initialStatus)
                .build();

        tradeRepository.save(trade);


        NotificationDTO notificationDTO = new NotificationDTO(
                NotificationType.TRADE_REQUESTED,
                me.getEmail(),
                other.getEmail(),
                trade.getId()
        );
        notificationService.notify(notificationDTO);

        return trade.getId();
    }

    @Override
    @Transactional
    public void acceptTrade(AcceptTradeRequest acceptTradeRequest, String username) {
        Member accepter = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Trade trade = tradeRepository.findById(acceptTradeRequest.getTradeId()).orElseThrow(TradeNotFoundException::new);
        Card card = cardRepository.findLockedById(trade.getCardId()).orElseThrow(CardNotFoundException::new);

        boolean canAccept = switch (trade.getStatus()) {
            case BUY_REQUESTED -> accepter.equals(trade.getSeller());
            case SELL_REQUESTED -> accepter.equals(trade.getBuyer());
            default -> false;
        };

        if (!canAccept) {
            throw new TradePermissionDeniedException();
        }

        trade.changeStatus(TradeStatus.ACCEPTED);
        card.changeSellStatus(SellStatus.TRADING);

        NotificationDTO notificationDTO = new NotificationDTO(
                NotificationType.TRADE_PAYMENT_REQUESTED,
                trade.getSeller().getEmail(),
                trade.getBuyer().getEmail(),
                trade.getId()
        );

        notificationService.notify(notificationDTO);
    }
}
