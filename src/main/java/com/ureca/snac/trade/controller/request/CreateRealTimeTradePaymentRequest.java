package com.ureca.snac.trade.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateRealTimeTradePaymentRequest {
    private Long tradeId;
    private Integer money;
    private Integer point;
}
