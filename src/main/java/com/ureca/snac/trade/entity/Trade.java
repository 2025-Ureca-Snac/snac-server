package com.ureca.snac.trade.entity;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trade",
        uniqueConstraints = @UniqueConstraint(name = "uk_trade_card_member", columnNames = {"card_id", "buyer_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", nullable = false)
    private Carrier carrier;

    @Column(name = "price_gb", nullable = false)
    private Integer priceGb; // 1기가 당 가격

    @Column(name = "data_amount", nullable = false)
    private Integer dataAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatus status;

    // 추가 (포인트/머니)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Builder
    private Trade(Long cardId, Member seller, Member buyer,
                  Carrier carrier, Integer priceGb, Integer dataAmount,
                  PaymentType paymentType, TradeStatus status) {
        this.cardId = cardId;
        this.seller = seller;
        this.buyer  = buyer;
        this.carrier = carrier;
        this.priceGb = priceGb;
        this.dataAmount = dataAmount;
        this.paymentType = paymentType;
        this.status = status;
    }

    // 거래 상태 변경
    public void changeStatus(TradeStatus status) {
        this.status = status;
    }
}
