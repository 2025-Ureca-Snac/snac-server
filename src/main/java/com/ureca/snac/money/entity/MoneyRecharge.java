package com.ureca.snac.money.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Integer paidAmountWon;

    // 외부 결제 API 리팩토링 근거 1
    // 역핣 분리, 충전과 결제

    @Enumerated(EnumType.STRING)
    private PaymentCategory pg;

    @Column(length = 50)
    private String pgMethod;

    @Column(unique = true, length = 64)
    private String pgOrderId;

    @Column(unique = true, length = 200)
    private String pgPaymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RechargeStatus status;

    private LocalDateTime paidAt;

    @Builder
    private MoneyRecharge(Long id, Member member, Integer paidAmountWon, PaymentCategory pg, String pgMethod, String pgOrderId, String pgPaymentKey, RechargeStatus status) {
        this.id = id;
        this.member = member;
        this.paidAmountWon = paidAmountWon;
        this.pg = pg;
        this.pgMethod = pgMethod;
        this.pgOrderId = pgOrderId;
        this.pgPaymentKey = pgPaymentKey;
        this.status = status;
    }

    public void confirmSuccess(String pgPaymentKey, String pgMethod) {
        this.status = RechargeStatus.SUCCESS;
        this.pgPaymentKey = pgPaymentKey;
        this.pgMethod = pgMethod;
        this.paidAt = LocalDateTime.now();
    }
}
