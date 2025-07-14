package com.ureca.snac.money.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "create")
public class MoneyRechargeResponse {
    @Schema(description = "우리 시스템 주문 번호")
    private final String orderId;

    @Schema(description = "주문 명", example = "스낵 머니 5000원 충전")
    private final String orderName;

    @Schema(description = "결제 금액", example = "5000")
    private final Integer amount;

    @Schema(description = "고객 이름 명", example = "김스낵")
    private final String customerName;
}
