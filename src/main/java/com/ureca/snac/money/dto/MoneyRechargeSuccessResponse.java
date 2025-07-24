package com.ureca.snac.money.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 스낵 머니 충전 성공시 응답 DTO
 */
@Schema(description = "스낵 머니 충전 성공 시 응답 DTO")
public record MoneyRechargeSuccessResponse(
        @Schema(description = "우리 시스템의 고유 주문번호")
        String orderId,

        @Schema(description = "토스페이먼츠의 결제 키")
        String paymentKey,

        @Schema(description = "실제 충전된 금액")
        Long amount
) {
    public static MoneyRechargeSuccessResponse of(
            String orderId, String paymentKey, Long amount) {
        return new MoneyRechargeSuccessResponse(orderId, paymentKey, amount);
    }
}
