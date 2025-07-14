package com.ureca.snac.money.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스낵 머지 충전 내역 기록 엔티티
 * Payment 엔티티가 결제를 담당하고
 * 이 엔티티는 역할 분리해서 회원, 결제 방법, 얼마 충전을 기록한다.
 */
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

    // 외부 결제 API 리팩토링
    // 역할 분리, 충전과 결제

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    private MoneyRecharge(Member member, Payment payment) {
        this.member = member;
        this.payment = payment;
        this.paidAmountWon = payment.getAmount();
    }

    /**
     * Payment 객체를 기반으로 머니 충전 내역 생성
     *
     * @param member  충전한 회원
     * @param payment 결제 정보
     * @return MoneyRecharge 객체
     */
    private static MoneyRecharge create(Member member, Payment payment) {
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("완료된 결제에 대해서 충전 내역 생성가능");
        }
        return new MoneyRecharge(member, payment);
    }
}
