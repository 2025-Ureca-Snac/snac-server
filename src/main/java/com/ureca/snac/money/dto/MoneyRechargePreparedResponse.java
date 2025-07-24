package com.ureca.snac.money.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "토스 결제 위젯 초기화를 위한 결제 준비 응답 DTO")
public record MoneyRechargePreparedResponse(

        @Schema(description = "우리 시스템 주문 번호")
        String orderId,

        @Schema(description = "주문 명", example = "스낵 머니 5000원 충전")
        String orderName,

        @Schema(description = "결제 금액", example = "5000")
        Long amount,

        @Schema(description = "고객 이름 명", example = "김스낵")
        String customerName,

        @Schema(description = "고객 이메일 (결제 내역 통지 및 위젯 편의성 제공)")
        String customerEmail
) {
}
