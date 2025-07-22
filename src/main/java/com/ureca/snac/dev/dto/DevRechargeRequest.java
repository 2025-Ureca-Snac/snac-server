package com.ureca.snac.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 개발용 강제 충전 요청 DTO
 * 토스과정 생략
 */
@Schema(description = "개발용 강제 충전")
public record DevRechargeRequest(
        @Schema(description = "충전할 사용자 이메일", example = "calmdown0111@gmail.com")
        String email,

        @Schema(description = "충전할 금액", example = "50000")
        Long amount
) {
}
