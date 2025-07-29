package com.ureca.snac.board.entity;

import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardInvalidStatusException;
import com.ureca.snac.board.exception.NotRealTimeSellCardException;
import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.exception.TradePaymentMismatchException;
import com.ureca.snac.trade.exception.TradePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeSelfRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.ureca.snac.board.entity.constants.CardCategory.REALTIME_SELL;
import static com.ureca.snac.board.entity.constants.SellStatus.*;
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
                .sellStatus(SOLD_OUT)
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

    // 리팩토링 코드
    /**
     * requiredStatus가 SELLING(판매글)일 땐
     *   본인 소유 불가(TradeSelfRequestException)
     * requiredStatus가 PENDING(구매글)일 땐
     *   반드시 본인 소유여야 함(TradePermissionDeniedException)
     */
    public void ensureCreatableBy(Member m, SellStatus requiredStatus) {
        boolean isOwner = this.member.equals(m);

        if (requiredStatus == SELLING && isOwner) {
            throw new TradeSelfRequestException();
        }
        if (requiredStatus == PENDING && !isOwner) {
            throw new TradePermissionDeniedException();
        }
    }

    /** 실시간 판매 카드인지 검증 */
    public void ensureRealTimeSellCategory() {
        if (this.cardCategory != REALTIME_SELL) {
            throw new NotRealTimeSellCardException();
        }
    }

    public void ensureSellStatus(SellStatus expected) {
        if (this.sellStatus != expected) {
            throw new CardInvalidStatusException();
        }
    }

    // SellStatus 변경
    public void markTrading() {
        ensureSellStatus(SELLING);
        this.sellStatus = TRADING;
    }

    public void markSelling() {
        ensureSellStatus(PENDING);
        this.sellStatus = SELLING;
    }

    public void ensurePaymentMatches(long money, long point) {
        long total = money + point;
        if (this.price.longValue() != total) {
            throw new TradePaymentMismatchException();
        }
    }
}
