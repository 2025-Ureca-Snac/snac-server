package com.ureca.snac.payment.service;

import com.ureca.snac.member.Member;
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
}
