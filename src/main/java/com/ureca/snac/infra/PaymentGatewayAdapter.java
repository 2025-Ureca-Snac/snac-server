package com.ureca.snac.infra;

import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;

/**
 * 어댑터 패턴 써서
 * 외부 결제 와 통신을 위한 인터페이스
 * 외부 시스템의 구현에 의존안하고 결제 승인 및 취소 사용
 */
public interface PaymentGatewayAdapter {
    /**
     * 결제 승인을 요청
     *
     * @param paymentKey 결제 건 식별키
     * @param orderId    우리 시스템의 고유 주문번호
     * @param amount     승인할 금액
     * @return 응답 정보
     */
    TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount);

    /**
     * 결제 취소를 요청 합니다
     *
     * @param paymentKey 결제건 식별키
     * @param reason     취소 사유
     * @return 우리 시스템의 표준 결제 취소응답 DTO
     */
    PaymentCancelResponse cancelPayment(String paymentKey, String reason);
}
