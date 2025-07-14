package com.ureca.snac.trade.service.response;

import com.ureca.snac.trade.entity.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeResponse {
    private Long tradeId;
    private TradeStatus status;
}
