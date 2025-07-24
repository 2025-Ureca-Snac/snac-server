package com.ureca.snac.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class CancelTradeDto {
    private String username;
    private TradeDto tradeDto;
}
