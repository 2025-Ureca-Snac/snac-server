package com.ureca.snac.payment.repository;

import com.ureca.snac.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * 고유 주문번호를 통해 Payment 조회
     * 결제 성공/ 실패 후 외부 결제 시스템으로 받은 주문번호로 원본 결제 찾는다.
     *
     * @param orderId 조회 주문번호
     * @return Payment 객체 optional
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * 결제 키를 사용하여 엔티티 조히
     *
     * @param paymentKey 토스가 발급한 결제 식별자
     * @return 조회된 Payment 담은 객체
     */
    Optional<Payment> findByPaymentKey(String paymentKey);
}
