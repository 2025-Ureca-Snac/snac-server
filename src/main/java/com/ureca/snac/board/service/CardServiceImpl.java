package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.board.service.response.CardResponse;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long createCard(Long memberId, CreateCardRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Card card = Card.builder().member(member)
                .sellStatus(SellStatus.SELLING)
                .cardCategory(request.getCardCategory())
                .carrier(request.getCarrier())
                .dataAmount(request.getDataAmount())
                .price(request.getPrice())
                .build();

        Card savedCard = cardRepository.save(card);

        return savedCard.getId();
    }

    @Override
    @Transactional
    public void updateCard(Long memberId, Long cardId, UpdateCardRequest updateCardRequest) {
        Card card = cardRepository.findByIdAndMemberId(cardId, memberId).orElseThrow(CardNotFoundException::new);

        card.update(
                updateCardRequest.getCardCategory(),
                updateCardRequest.getCarrier(),
                updateCardRequest.getDataAmount(),
                updateCardRequest.getPrice()
        );
    }

    @Override
    public ScrollCardResponse scrollCards(CardCategory cardCategory, Carrier carrier, PriceRange priceRange, int size, Long lastCardId, LocalDateTime lastUpdatedAt) {
        List<Card> raw = cardRepository.scroll(cardCategory, carrier, priceRange, size + 1, lastCardId, lastUpdatedAt);

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
    public void deleteCard(Long memberId, Long cardId) {
        Card card = cardRepository.findByIdAndMemberId(cardId, memberId).orElseThrow(CardNotFoundException::new);

        cardRepository.delete(card);
    }
}
