package com.ureca.snac.payment.entity;

import com.ureca.snac.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

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
     * prepare 상태의 객체 생성 팩토리 메소드
     * Builder 반환 대신 Payment 객체 반환
     * 외부에서 Builder 모름
     *
     * @param amount 결제 요청 금액
     * @return Payment 객체
     */
    public static Payment prepare(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("결제 요청 금액 필수, 0보다 커야 한다.");
        }
        return Payment.builder()
                .orderId("snac_order_" + UUID.randomUUID())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();
    }

    public void complete(String paymentKey, String method, OffsetDateTime paidAt) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.paidAt = paidAt;
        this.status = PaymentStatus.SUCCESS;
    }

    public void cancel(String reason) {
        this.cancelReason = reason;
        this.status = PaymentStatus.CANCELED;
    }
}
