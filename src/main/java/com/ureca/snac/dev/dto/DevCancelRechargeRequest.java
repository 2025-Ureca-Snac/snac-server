package com.ureca.snac.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 개발용 강제 충전 취소 요청 DTO
 */
@Schema(description = "개발용 강제 충전 취소 요청 DTO")
public record DevCancelRechargeRequest(
        @Schema(description = "취소할 충전의 Payment ID", example = "1")
        Long paymentId,

        @Schema(description = "취소 사유", example = "개발용 취소")
        String reason
) {
}
