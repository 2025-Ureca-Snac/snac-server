package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CardService {

    /**
     * 새로운 판매글 또는 구매글을 등록합니다.
     *
     * @param username 등록자 이메일
     * @param request  등록할 카드 정보
     * @return 생성된 카드의 식별자(ID)
     */
    Long createCard(String username, CreateCardRequest request);

    /**
     * 기존 판매글 또는 구매글의 정보를 수정합니다.
     *
     * @param username          요청자 이메일
     * @param cardId            수정할 카드 ID
     * @param updateCardRequest 수정할 카드 정보
     */
    void updateCard(String username, Long cardId, UpdateCardRequest updateCardRequest);

    /**
     * 조건에 맞는 판매글 또는 구매글 목록을 커서 기반으로 조회합니다.
     *
     * @param cardCategory  카드 분류 (SELL 또는 BUY)
     * @param carrier       통신사 필터 (선택)
     * @param priceRange    가격대 필터 (복수 선택 가능)
     * @param size          조회할 데이터 개수
     * @param lastCardId    커서: 마지막으로 조회된 카드 ID (선택)
     * @param lastUpdatedAt 커서: 마지막으로 조회된 카드의 수정 시각 (선택)
     * @return 스크롤 방식으로 응답하는 카드 목록과 다음 페이지 존재 여부
     */
    ScrollCardResponse scrollCards(CardCategory cardCategory, Carrier carrier, List<PriceRange> priceRange, SellStatusFilter sellStatusFilter, Boolean highRatingFirst,
                                   Integer size, Long lastCardId, LocalDateTime lastUpdatedAt);

    /**
     * 카드(판매글/구매글)를 삭제합니다.
     *
     * @param username 요청자 이메일
     * @param cardId   삭제할 카드 ID
     */
    void deleteCard(String username, Long cardId);

    void deleteCardByTrade(Long cardId);

    CardDto createRealtimeCard(String username, CreateRealTimeCardRequest request);

    List<CardDto> findRealtimeCardsByFilter(BuyerFilterRequest filter);

    List<CardDto> findByMemberUsernameAndSellStatusAndCardCategory(String username, SellStatus sellStatus, CardCategory cardCategory);
}

