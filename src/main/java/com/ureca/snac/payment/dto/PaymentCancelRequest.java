package com.ureca.snac.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentCancelRequest(
        @Schema(description = "결제 취소 사유", example = "고객 변심")
        @NotBlank(message = "취소 사유를 반드시 입력하세요")
        String reason
) {
}
