package com.ureca.snac.board.service.response;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardResponse {
    private Long id;
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

    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getMember().getName(),
                card.getMember().getEmail(),
                card.getMember().getRatingScore(),
                card.getSellStatus(),
                card.getCardCategory(),
                card.getCarrier(),
                card.getDataAmount(),
                card.getPrice(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }
}
