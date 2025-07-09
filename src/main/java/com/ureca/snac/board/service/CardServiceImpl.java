package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
