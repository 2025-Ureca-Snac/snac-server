package com.ureca.snac.settlement.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 머니 정산 요청 DTO
 */
public record SettlementRequest(

        @Schema(description = "정산 금액")
        @NotNull(message = "정산 금액을 입력해주세요")
        Long amount,

        @Schema(description = "입금받을 계좌번호")
        @NotBlank(message = "계좌번호를 입력해주세요")
        String accountNumber
) {
}
