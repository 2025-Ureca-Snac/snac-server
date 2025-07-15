package com.ureca.snac.trade.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTradeRequest {
    private Long cardId;
    private Integer money;
    private Integer point;
}
