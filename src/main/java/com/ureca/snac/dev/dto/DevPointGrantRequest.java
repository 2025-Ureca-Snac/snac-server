package com.ureca.snac.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개발용 강제 포인트 적립 요청")
public record DevPointGrantRequest(
        @Schema(description = "포인트를 적립할 사용자의 이메일", example = "calmdown0111@gmail.com")
        String email,

        @Schema(description = "지급할 포인트 금액", example = "5000")
        Long amount,

        @Schema(description = "지급 사유(자산 내역 표시)", example = "신규 가입 이벤트")
        String reason
) {
}
