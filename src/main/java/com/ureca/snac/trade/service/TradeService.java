package com.ureca.snac.trade.service;

import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.controller.request.TradeRequest;
import com.ureca.snac.trade.dto.TradeSide;

public interface TradeService {
    Long requestTrade(TradeRequest tradeRequest, String username, TradeSide tradeSide);
    void acceptTrade(AcceptTradeRequest acceptTradeRequest, String username);
}
