package com.ureca.snac.payment.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import com.ureca.snac.payment.exception.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;
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

    // 비밀통로
    public static Payment createForDev(Member member, Long amount) {
        return Payment.builder()
                .member(member)
                .orderId("dev_order_" + UUID.randomUUID())
                .amount(amount)
                .method("계좌이체")
                .status(PaymentStatus.SUCCESS)
                .paymentKey("dev_payment_key_" + System.currentTimeMillis())
                .paidAt(OffsetDateTime.now())
                .build();
    }

    // 핵심 비즈니스 메소드

    // 상태 완료
    public void complete(String paymentKey, String method, OffsetDateTime paidAt) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.paidAt = paidAt;
        this.status = PaymentStatus.SUCCESS;
    }

    // 상태 취소
    public void cancel(String reason) {
        this.cancelReason = reason;
        this.status = PaymentStatus.CANCELED;
    }

    // 유효성 검증 메소드
    public void validateForConfirmation(Member member, Long amount) {
        if (isAlreadyProcessed()) {
            throw new PaymentAlreadyProcessedPaymentException();
        }
        if (!isOwner(member)) {
            throw new PaymentOwnershipMismatchException();
        }
        if (!isAmount(amount)) {
            throw new PaymentAmountMismatchException();
        }
    }

    public void validateForCancellation(Member member, Long currentUserBalance) {
        // 이미 썻다 잔액 확인
        if (currentUserBalance < this.getAmount()) {
            throw new AlreadyUsedRechargeCannotCancelException();
        }
        // 취소 불가능한지
        if (this.status != PaymentStatus.SUCCESS) {
            throw new PaymentNotCancellableException();
        }

        if (!isOwner(member)) {
            throw new PaymentOwnershipMismatchException();
        }

        if (isCancellationPeriodExpired()) {
            throw new PaymentPeriodExpiredException();
        }
    }

    private boolean isCancellationPeriodExpired() {
        if (this.method == null || this.paidAt == null) {
            return true;
        }
        OffsetDateTime now = OffsetDateTime.now();

        switch (this.method) {
            case "휴대폰":
                return this.paidAt.getMonth() != now.getMonth() ||
                        this.paidAt.getYear() != now.getYear();

            case "가상계좌":
                // API 취소 불가능
                return true;

            case "카드":
            default:
                // 카드 별도 기한 정책 없음
                return false;
        }
    }

    // 내부 상태 조회용 헬퍼 메소드
    // 이미 처리된 건인지 증명
    private boolean isAlreadyProcessed() {
        return this.status != PaymentStatus.PENDING;
    }

    // 소유주 검증
    private boolean isOwner(Member member) {
        if (this.member == null || member == null) {
            return false;
        }
        return Objects.equals(this.member.getId(), member.getId());
    }

    // 기록 금액 검증
    private boolean isAmount(Long amount) {
        return this.amount.equals(amount);
    }
}
