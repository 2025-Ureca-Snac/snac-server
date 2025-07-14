package com.ureca.snac.trade.service;

import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.notification.dto.NotificationDTO;
import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.exception.TradePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeSelfRequestException;
import com.ureca.snac.trade.exception.DuplicateTradeRequestException;

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
    public Long requestTradeAsBuyer(TradeRequest tradeRequest, String username) {
        Card card = cardRepository.findById(tradeRequest.getCardId()).orElseThrow(CardNotFoundException::new);
        Member buyer = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Member seller = memberRepository.findByEmail(card.getMember().getEmail()).orElseThrow(MemberNotFoundException::new);

        if (card.getMember().equals(buyer))
            throw new TradeSelfRequestException();

        if (card.getCardCategory() != CardCategory.SELL)
            throw new IllegalArgumentException("판매글이 아닌 글에는 구매자가 요청할 수 없습니다.");

        Trade trade = Trade.builder().cardId(card.getId()).seller(seller)
                .buyer(buyer).carrier(card.getCarrier()).priceGb(card.getPrice())
                .dataAmount(card.getDataAmount()).status(TradeStatus.BUY_REQUESTED)
                .build();

        tradeRepository.save(trade);

        NotificationDTO notificationDTO = new NotificationDTO(NotificationType.TRADE_REQUESTED, buyer.getEmail(), seller.getEmail(), trade.getId());

        notificationService.notify(notificationDTO);

        return trade.getId();
    }

    @Override
    @Transactional
    public Long requestTradeAsSeller(TradeRequest tradeRequest, String username) {
        Card card = cardRepository.findById(tradeRequest.getCardId()).orElseThrow(CardNotFoundException::new);
        Member seller = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Member buyer = memberRepository.findByEmail(card.getMember().getEmail()).orElseThrow(MemberNotFoundException::new);

        if (card.getMember().equals(seller))
            throw new TradeSelfRequestException();

        if (card.getCardCategory() != CardCategory.BUY)
            throw new IllegalArgumentException("구매글이 아닌 글에는 판매자가 요청할 수 없습니다.");

        tradeRepository.findByCardIdAndBuyerIdAndStatusNot(
                card.getId(), seller.getId(), TradeStatus.CANCELED
        ).ifPresent(t -> { throw new DuplicateTradeRequestException(); });

        Trade trade = Trade.builder()
                .cardId(card.getId())
                .seller(seller)
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(TradeStatus.SELL_REQUESTED)
                .build();
        tradeRepository.save(trade);


        NotificationDTO notificationDTO = new NotificationDTO(
                NotificationType.TRADE_REQUESTED,
                seller.getEmail(),
                buyer.getEmail(),
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

        boolean canAccept = switch (trade.getStatus()) {
            case BUY_REQUESTED  -> accepter.equals(trade.getSeller());
            case SELL_REQUESTED  -> accepter.equals(trade.getBuyer());
            default -> false;
        };

        if (!canAccept) {
            throw new TradePermissionDeniedException();
        }

        trade.changeStatus(TradeStatus.ACCEPTED);

        NotificationDTO notificationDTO = new NotificationDTO(
                NotificationType.TRADE_PAYMENT_REQUESTED,
                trade.getSeller().getEmail(),
                trade.getBuyer().getEmail(),
                trade.getId()
        );

        notificationService.notify(notificationDTO);
    }
}
