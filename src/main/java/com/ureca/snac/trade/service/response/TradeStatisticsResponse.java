package com.ureca.snac.trade.service.response;

import com.ureca.snac.board.entity.constants.Carrier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeStatisticsResponse {
    private Carrier carrier;
    private Double avgPricePerGb;
}
