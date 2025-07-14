package com.ureca.snac.money.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import com.ureca.snac.payments.TossConfirmResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "money_recharge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoneyRecharge extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recharge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long paidAmountWon;

    // 외부 결제 API 리팩토링 근거 1
    // 역핣 분리, 충전과 결제

    @Enumerated(EnumType.STRING)
    private PaymentCategory pg;

    @Column(unique = true, length = 64)
    private String pgOrderId;

    @Column(unique = true, length = 200)
    private String pgPaymentKey;

    private String pgMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RechargeStatus status;

    private OffsetDateTime paidAt;

    @Builder
    private MoneyRecharge(Member member, Long paidAmountWon, PaymentCategory pg, String pgOrderId) {
        this.member = member;
        this.paidAmountWon = paidAmountWon;
        this.pg = pg;
        this.pgOrderId = pgOrderId;
        this.status = RechargeStatus.PENDING;
    }

    public void complete(TossConfirmResponse tossConfirmResponse) {
        this.status = RechargeStatus.SUCCESS;
        this.pgPaymentKey = tossConfirmResponse.paymentKey();
        this.pgMethod = tossConfirmResponse.method();
        this.paidAt = tossConfirmResponse.approvedAt();
    }
}
