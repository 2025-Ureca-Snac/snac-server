package com.ureca.snac.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentFailureRequest(
        @Schema(description = "토스 페이먼츠가 반환한 에러코드",
                example = "INVALID_CARD_INFO")
        @NotBlank
        String errorCode,

        @Schema(description = "토스페이먼츠가 반환한 에러 메시지",
                example = "카드 정보가 유효하지 않습니다")
        @NotBlank
        String errorMessage,

        @Schema(description = "실패한 주문의 ID")
        @NotBlank
        String orderId
) {
}
