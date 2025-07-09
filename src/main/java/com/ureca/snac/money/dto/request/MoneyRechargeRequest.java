package com.ureca.snac.money.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoneyRechargeRequest {
    @Schema(description = "충전 요청 금액", example = "5000")
    private Integer amount;
}
