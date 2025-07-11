package com.ureca.snac.trade.service;

import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.exception.TradeSelfRequestException;
import com.ureca.snac.trade.exception.DuplicateTradeRequestException;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.exception.TradePermissionDeniedException;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.notification.entity.NotificationType;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.dto.TradeRequest;
import com.ureca.snac.trade.dto.TradeResponse;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepo;
    private final CardRepository cardRepo;
    private final MemberRepository memberRepo;        // proxy용
    private final NotificationService notification;

    // 거래 신청
    // 판매/구매 카드를 기반으로 거래 요청 생성
    // 진행 상황에 따라 RabbitMq 알림 발행
    @Override
    @Transactional
    public TradeResponse requestTrade(TradeRequest dto, Long requesterId) {
        // 거래 대상 카드 조회 및 유효성 검증
        Card card = cardRepo.findById(dto.getCardId())
                .orElseThrow(CardNotFoundException::new);

        if (card.getMember().getId().equals(requesterId))
            throw new TradeSelfRequestException(); // 본인 글에 거래요청 x

        tradeRepo.findByCardIdAndBuyerIdAndStatusNot(
                dto.getCardId(), requesterId, TradeStatus.CANCELED
        ).ifPresent(t -> { throw new DuplicateTradeRequestException(); }); // 이미 동일 멤버가 요청한 거래가 있음

        // 판매자 구매자 역할 결정 lazy proxy
        Member seller = (card.getCardCategory() == CardCategory.SELL)
                        ? card.getMember()
                        : memberRepo.getReferenceById(requesterId);
        Member buyer  = (card.getCardCategory() == CardCategory.BUY)
                        ? card.getMember()
                        : memberRepo.getReferenceById(requesterId);

        Trade trade = Trade.builder()
                .cardId(card.getId())
                .seller(seller)
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())          // 단위 가격
                .dataAmount(card.getDataAmount())
                .status(TradeStatus.REQUESTED)
                .build();

        tradeRepo.save(trade);

        // 알림 (글 작성자가 'to', 신청자가 'from')
        notification.notify(
                memberRepo.getReferenceById(requesterId),
                card.getMember(),
                NotificationType.TRADE_REQUESTED,
                trade);

        return new TradeResponse(trade.getId(), trade.getStatus());
    }

    @Override
    @Transactional
    public TradeResponse requestTradeByEmail(TradeRequest dto, String email) {

        // email → Member 조회 (id 포함)
        Member requester = memberRepo.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        Long requesterId = requester.getId();

        // 기존 requestTrade 로직 재사용
        return requestTrade(dto, requesterId);
    }

    // 수락
    // 요청된 거래를 수락 상태로 변경하며 권한 검증.
    // 진행 상황에 따라 RabbitMq 알림 발행
    @Override @Transactional
    public void acceptTrade(Long tradeId, Long accepterId) {
        // REQUESTED 상태의 trade 조회
        Trade trade = tradeRepo.findByIdAndStatus(tradeId, TradeStatus.REQUESTED)
                .orElseThrow(TradeNotFoundException::new);

        // 수락 권한 확인
        boolean isSellerAccept = trade.getSeller().getId().equals(accepterId)
                                 && !trade.getSeller().getId().equals(trade.getBuyer().getId());
        boolean isBuyerAccept  = trade.getBuyer().getId().equals(accepterId)
                                 && !trade.getBuyer().getId().equals(trade.getSeller().getId());

        if (!isSellerAccept && !isBuyerAccept)
            throw new TradePermissionDeniedException();

        trade.changeStatus(TradeStatus.ACCEPTED);

        // 신청자에게 알림 발송
        Member from = memberRepo.getReferenceById(accepterId);
        Member to   = isSellerAccept ? trade.getBuyer() : trade.getSeller();

        notification.notify(from, to, NotificationType.TRADE_ACCEPTED, trade);
    }

    @Override @Transactional
    public void acceptTradeByEmail(Long tradeId, String email) {

        Member accepter = memberRepo.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        acceptTrade(tradeId, accepter.getId());
    }
}