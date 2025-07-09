package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;

import java.time.LocalDateTime;
import java.util.List;

public interface CardRepositoryCustom {
    List<Card> scroll(CardCategory cardCategory, Carrier carrier, PriceRange priceRange, int size, Long lastCardId, LocalDateTime localDateTime);
}
