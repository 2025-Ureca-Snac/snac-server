package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.response.ScrollCardResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CardService {

    Long createCard(Long memberId, CreateCardRequest request);

    void updateCard(Long memberId, Long cardId, UpdateCardRequest updateCardRequest);

    ScrollCardResponse scrollCards(CardCategory cardCategory, Carrier carrier, List<PriceRange> priceRange, int size, Long lastCardId, LocalDateTime lastUpdatedAt);

    void deleteCard(Long memberId, Long cardId);
}

