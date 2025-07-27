package com.ureca.snac.payment.service;

import com.ureca.snac.payment.entity.Payment;

public interface PaymentRecoveryService {
    /**
     * 외부 결제 성공 후 내부 DB 처리 실패 시 호출
     * 새로운 트랜잭션에서 실행되어야 한다
     * 롤백에 영향 받지않음
     *
     * @param payment 실패한 엔티티
     * @param e       예외
     */
    void processInternalFailure(Payment payment, Exception e);
}
