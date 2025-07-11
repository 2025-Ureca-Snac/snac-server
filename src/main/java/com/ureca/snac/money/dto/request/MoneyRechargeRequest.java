package com.ureca.snac.money.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoneyRechargeRequest {

    @Schema(description = "충전 요청 금액", example = "5000")
    @NotNull(message = "충전 금액은 필수 항목입니다.")
    private Integer amount;
}
