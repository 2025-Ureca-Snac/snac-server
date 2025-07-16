package com.ureca.snac.trade.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScrollTradeResponse {
    List<TradeResponse> trades;
    boolean hasNext;
}
