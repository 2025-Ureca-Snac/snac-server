package com.ureca.snac.payment.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * 결제 취소 성공 시 서비스 계층이 반환하는 DTO
 * 우리 시스템의 데이터 구조
 *
 * @param paymentKey     결제 취소할 고유 키
 * @param canceledAmount 실제로 취소된 금액
 * @param cancelAt       실제로 취소한 시각
 * @param reason         취소 사유
 */
@Builder
public record PaymentCancelResponse(
        String paymentKey,
        Long canceledAmount,
        OffsetDateTime cancelAt,
        String reason
) {
}
