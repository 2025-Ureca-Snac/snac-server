package com.ureca.snac.payment.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import com.ureca.snac.payment.exception.PaymentNotCancellableException;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.ureca.snac.common.BaseCode.INVALID_INPUT;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(unique = true)
    private String paymentKey;

    private String method;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private OffsetDateTime paidAt;

    private String cancelReason;

    /**
     * prepare 상태의 객체 생성 팩토리 메소드 + private 빌더 객체 생성 통제
     * Builder 반환 대신 Payment 객체 반환
     * 외부에서 Builder 모름
     *
     * @param amount 결제 요청 금액
     * @return Payment 객체
     */
    public static Payment prepare(Member member, Long amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException(INVALID_INPUT);
        }
        return Payment.builder()
                .member(member)
                .orderId("snac_order_" + UUID.randomUUID())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();
    }

    // 상태 완료
    public void complete(String paymentKey, String method, OffsetDateTime paidAt) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.paidAt = paidAt;
        this.status = PaymentStatus.SUCCESS;
    }

    // 상태 취소
    public void cancel(String reason) {
        if (!isCancellable()) {
            throw new PaymentNotCancellableException();
        }
        this.cancelReason = reason;
        this.status = PaymentStatus.CANCELED;
    }

    // 취소 검증
    public boolean isCancellable() {
        return this.status == PaymentStatus.SUCCESS;
    }

    // 이미 처리된 건인지 증명
    public boolean isAlreadyProcessed() {
        return this.status != PaymentStatus.PENDING;
    }

    // 소유주 검증
    public boolean validateOwner(Member member) {
        if (this.member == null || member == null) {
            return true;
        }
        return !this.member.getId().equals(member.getId());
    }

    // 기록 금액 검증
    public boolean isAmount(Long amount) {
        return this.amount.equals(amount);
    }
}
