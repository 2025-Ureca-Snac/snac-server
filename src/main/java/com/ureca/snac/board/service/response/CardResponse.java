package com.ureca.snac.board.service.response;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CardResponse {
    private Long id;

    private Long authorId;
    private String name;
    private String email;
    private Integer ratingScore;

    private SellStatus sellStatus;
    private CardCategory cardCategory;
    private Carrier carrier;
    private Integer dataAmount;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean isFavorite;

    private CardResponse(Long id, Long authorId, String name, String email, Integer ratingScore,
                         SellStatus sellStatus, CardCategory cardCategory, Carrier carrier,
                         Integer dataAmount, Integer price, LocalDateTime createdAt,
                         LocalDateTime updatedAt, boolean isFavorite) {
        this.id = id;
        this.authorId = authorId;
        this.name = name;
        this.email = email;
        this.ratingScore = ratingScore;
        this.sellStatus = sellStatus;
        this.cardCategory = cardCategory;
        this.carrier = carrier;
        this.dataAmount = dataAmount;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isFavorite = isFavorite;
    }

    // 단골 여부가 필요없는 곳에서 쓰게
    public static CardResponse from(Card card) {
        return from(card, false);
    }

    // 단골 여부가 필요할 때 쓰게
    public static CardResponse from(Card card, boolean isFavorite) {
        return new CardResponse(
                card.getId(),
                card.getMember().getId(),
                card.getMember().getName(),
                card.getMember().getEmail(),
                card.getMember().getRatingScore(),
                card.getSellStatus(),
                card.getCardCategory(),
                card.getCarrier(),
                card.getDataAmount(),
                card.getPrice(),
                card.getCreatedAt(),
                card.getUpdatedAt(),
                isFavorite
        );
    }
}
