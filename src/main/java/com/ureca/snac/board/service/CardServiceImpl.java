package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardAlreadySoldOutException;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.board.service.response.CardResponse;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ureca.snac.board.entity.constants.CardCategory.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long createCard(String username, CreateCardRequest request) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);

        Card card = getBuildCard(request, member);

        Card savedCard = cardRepository.save(card);

        return savedCard.getId();
    }

    @Transactional
    public CardDto createRealtimeCard(String username, CreateRealTimeCardRequest request) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(MemberNotFoundException::new);

        Card card = Card.builder()
                .member(member)
                .sellStatus(SellStatus.SELLING)
                .cardCategory(REALTIME_SELL)
                .carrier(request.getCarrier())
                .dataAmount(request.getDataAmount())
                .price(request.getPrice())
                .build();

        Card savedCard = cardRepository.save(card);

        return CardDto.from(card);
    }

    public List<CardDto> findRealtimeCardsByFilter(BuyerFilterRequest filter) {
        return cardRepository.findRealtimeCardsByFilter(filter).stream()
                .map(CardDto::from)
                .toList();
    }

    private static Card getBuildCard(CreateCardRequest request, Member member) {
        SellStatus sellStatus = (request.getCardCategory() == BUY) ? SellStatus.PENDING : SellStatus.SELLING;

        return Card.builder().member(member)
                .sellStatus(sellStatus)
                .cardCategory(request.getCardCategory())
                .carrier(request.getCarrier())
                .dataAmount(request.getDataAmount())
                .price(request.getPrice())
                .build();
    }

    @Override
    @Transactional
    public void updateCard(String username, Long cardId, UpdateCardRequest updateCardRequest) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Card card = cardRepository.findLockedByIdAndMember(cardId, member).orElseThrow(CardNotFoundException::new);

        if (card.getSellStatus() == SellStatus.TRADING) {
            throw new CardAlreadyTradingException();
        }

        if (card.getSellStatus() == SellStatus.SOLD_OUT) {
            throw new CardAlreadySoldOutException();
        }

        card.update(
                updateCardRequest.getCardCategory(),
                updateCardRequest.getCarrier(),
                updateCardRequest.getDataAmount(),
                updateCardRequest.getPrice()
        );
    }

    @Override
    public ScrollCardResponse scrollCards(CardCategory cardCategory, Carrier carrier, List<PriceRange> priceRanges, SellStatusFilter sellStatusFilter, Boolean highRatingFirst,
                                          Integer size, Long lastCardId, LocalDateTime lastUpdatedAt) {

        List<Card> raw = cardRepository.scroll(cardCategory, carrier, priceRanges, sellStatusFilter, highRatingFirst,
                size + 1, lastCardId, lastUpdatedAt);

        boolean hasNext = raw.size() > size;

        List<Card> slice = raw.stream()
                .limit(size)
                .toList();

        List<CardResponse> dtoList = slice.stream()
                .map(CardResponse::from)
                .toList();

        return new ScrollCardResponse(dtoList, hasNext);
    }

    @Override
    @Transactional
    public void deleteCard(String username, Long cardId) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Card card = cardRepository.findLockedByIdAndMember(cardId, member).orElseThrow(CardNotFoundException::new);

        if (card.getSellStatus() == SellStatus.TRADING) {
            throw new CardAlreadyTradingException();
        }

        if (card.getSellStatus() == SellStatus.SOLD_OUT) {
            throw new CardAlreadySoldOutException();
        }

        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public void deleteCardByTrade(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
