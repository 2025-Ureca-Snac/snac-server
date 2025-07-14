package com.ureca.snac.money.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoneyRechargeRequest {

    @Schema(description = "충전 요청 금액", example = "5000")
    @NotNull(message = "충전 금액은 필수 항목입니다.")
    @Min(value = 1000, message = "충전 금액은 1000원 이상이어야 한다.")
    private Long amount;
}
