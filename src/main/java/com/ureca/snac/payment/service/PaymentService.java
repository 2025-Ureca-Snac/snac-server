package com.ureca.snac.payment.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.entity.Payment;

/**
 * 결제 도메인의 비즈니스 서비스 인터페이스
 * 도메인 결제 책인 분리
 */
public interface PaymentService {
    /**
     * 결제 위한 사전 준비 PENDING 상태 Payment 객체
     * 결제 생성에 집중
     *
     * @param amount 결제 요청 금액
     * @return 생성된 객체
     */
    Payment preparePayment(Member member, Long amount);

    /**
     * 결제 취소 기능
     *
     * @param paymentKey 취소할 결제 ID
     * @param reason     취소 사유
     * @param email      취소요청한 사람
     * @return 결제 취소의 상세 정보 PaymentCancelResponse DTO
     */
    PaymentCancelResponse cancelPayment(String paymentKey, String reason, String email);
}
