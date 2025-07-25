package com.ureca.snac.board.dto;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class CardDto {
    private Long cardId;
    private String name;
    private String email;
    private SellStatus sellStatus;
    private CardCategory cardCategory;
    private Carrier carrier;
    private Integer dataAmount;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CardDto from(Card card) {
        return new CardDto(
                card.getId(),
                card.getMember().getName(),
                card.getMember().getEmail(),
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
