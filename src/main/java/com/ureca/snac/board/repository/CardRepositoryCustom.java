package com.ureca.snac.board.repository;

import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;

import java.time.LocalDateTime;
import java.util.List;

public interface CardRepositoryCustom {
    List<Card> scroll(CardCategory cardCategory,
                      Carrier carrier,
                      List<PriceRange> priceRanges,
                      SellStatusFilter sellStatusFilter,
                      Boolean highRatingFirst,
                      Integer size,
                      Long lastCardId,
                      LocalDateTime lastUpdatedAt);
}
