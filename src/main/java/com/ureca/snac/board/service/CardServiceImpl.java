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
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.board.service.response.CardResponse;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.favorite.repository.FavoriteRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.ureca.snac.board.entity.constants.CardCategory.BUY;
import static com.ureca.snac.board.entity.constants.CardCategory.REALTIME_SELL;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

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
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);

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

        card.ensureSellStatus(SellStatus.SELLING); // 판매중인 글만 삭제 가능

        card.update(
                updateCardRequest.getCardCategory(),
                updateCardRequest.getCarrier(),
                updateCardRequest.getDataAmount(),
                updateCardRequest.getPrice()
        );
    }

    @Override
    public ScrollCardResponse scrollCards(CardCategory cardCategory, Carrier carrier, PriceRange priceRange, SellStatusFilter sellStatusFilter, Boolean highRatingFirst,
                                          Integer size, Long lastCardId, LocalDateTime lastUpdatedAt,
                                          Boolean favoriteOnly, String username) {

        List<Card> raw = cardRepository.scroll(cardCategory, carrier, priceRange, sellStatusFilter, highRatingFirst,
                size + 1, lastCardId, lastUpdatedAt, favoriteOnly, username);

        // 단골 여부 확인 N+1 방지
        Set<Long> favoriteAuthorIds = Collections.emptySet();

        // 로그인한 사용자에다가 조회된 카드가 있을 때
        if (username != null && !raw.isEmpty()) {
            // 작성자 ID 추출
            Set<Long> authorIdSet = new HashSet<>();

            for (Card card : raw) {
                authorIdSet.add(card.getMember().getId());
            }
            List<Long> authorIds = new ArrayList<>(authorIdSet);

            // 현재 로그인 사용자
            Member currentMember = memberRepository.findByEmail(username)
                    .orElseThrow(MemberNotFoundException::new);

            // 내가 단골로 추가한 작성자 ID 목록을 조회
            favoriteAuthorIds =
                    favoriteRepository.findFavoriteToMemberIdsByFromMember(currentMember, authorIds);
        }
        boolean hasNext = raw.size() > size;

        List<CardResponse> dtoList = new ArrayList<>();

        int loopSize = hasNext ? size : raw.size();

        for (int i = 0; i < loopSize; i++) {
            Card card = raw.get(i);
            // 작성자가 내가 단골로 추가햇는지
            boolean isFavorite =
                    favoriteAuthorIds.contains(card.getMember().getId());

            CardResponse dto = CardResponse.from(card, isFavorite);
            dtoList.add(dto);
        }

        return new ScrollCardResponse(dtoList, hasNext);
    }

    @Override
    @Transactional
    public void deleteCard(String username, Long cardId) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Card card = cardRepository.findLockedByIdAndMember(cardId, member).orElseThrow(CardNotFoundException::new);

        card.ensureDeletable();

        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public void deleteCardByRealTime(String username, Long cardId) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
//        Card card = cardRepository.findLockedByIdAndMember(cardId, member).orElseThrow(CardNotFoundException::new);
//        cardRepository.delete(card);

        cardRepository.findLockedByIdAndMember(cardId, member).ifPresent(cardRepository::delete);
    }

    @Transactional
    public List<CardDto> findByMemberUsernameAndSellStatusesAndCardCategory(String username, List<SellStatus> sellStatuses, CardCategory cardCategory) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);

//        return cardRepository.findLockedByMemberAndSellStatusInAndCardCategory(member, sellStatuses, cardCategory) -> 락 해제
        return cardRepository.findByMemberAndSellStatusInAndCardCategory(member, sellStatuses, cardCategory)
                .stream()
                .map(CardDto::from)
                .toList();
    }

    @Override
    public CardResponse findCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .map(CardResponse::from)
                .orElseThrow(CardNotFoundException::new);
    }

    @Override
    public List<CardResponse> getSellingCardsByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        return cardRepository.findByMemberAndSellStatusOrderByUpdatedAtDesc(member, SellStatus.SELLING)
                .stream()
                .map(CardResponse::from)
                .toList();
    }

    @Override
    public List<CardDto> findAllDevCard() {
        return cardRepository.findAll()
                .stream()
                .map(CardDto::from)
                .toList();
    }

    @Override
    public ScrollCardResponse getCardsByOwner(String username, CardCategory cardCategory, int size, Long lastCardId, LocalDateTime lastUpdatedAt) {
        // 1) 로그인 사용자 조회
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(MemberNotFoundException::new);

        // 2) 페이징을 위해 요청 사이즈 + 1 로 한 건 더 가져오기
        List<Card> cards = cardRepository.scrollByOwnerAndCategory(
                member,
                cardCategory,
                size + 1,
                lastCardId,
                lastUpdatedAt
        );

        // 3) 다음 페이지 존재 여부 판단
        boolean hasNext = cards.size() > size;


        // 4) DTO 변환
        List<CardResponse> dtos = cards.stream()
                .limit(size)
                .map(CardResponse::from)
                .toList();

        // 5) 응답 생성
        return new ScrollCardResponse(dtos, hasNext);
    }

    @Override
    @Transactional
    public void deleteCardByTrade(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
