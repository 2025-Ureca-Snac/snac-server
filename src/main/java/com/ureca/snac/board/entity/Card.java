package com.ureca.snac.board.entity;

import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "card",
        indexes = {
                @Index(name = "idx_card_updated_at_id", columnList = "updated_at, card_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseTimeEntity {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "sell_status", nullable = false)
    private SellStatus sellStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_category", nullable = false)
    private CardCategory cardCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", nullable = false)
    private Carrier carrier;

    @Column(name = "data_amount", nullable = false)
    private Integer dataAmount;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Builder
    private Card(Member member, SellStatus sellStatus, CardCategory cardCategory, Carrier carrier, Integer dataAmount, Integer price) {
        this.member = member;
        this.sellStatus = sellStatus;
        this.cardCategory = cardCategory;
        this.carrier = carrier;
        this.dataAmount = dataAmount;
        this.price = price;
    }

    public static Card createFake(Member owner, Carrier carrier, Integer dataAmount,
                                  Long price, CardCategory category) {
        return Card.builder()
                .member(owner)
                .sellStatus(SellStatus.SOLD_OUT)
                .cardCategory(category)
                .carrier(carrier)
                .dataAmount(dataAmount)
                .price(price.intValue())
                .build();
    }

    public void update(CardCategory cardCategory, Carrier carrier, Integer dataAmount, Integer price) {
        this.cardCategory = cardCategory;
        this.carrier = carrier;
        this.dataAmount = dataAmount;
        this.price = price;
    }

    public void changeSellStatus(SellStatus sellStatus) {
        this.sellStatus = sellStatus;
    }
}
