package com.ureca.snac.trade.service;

import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.controller.request.TradeRequest;

public interface TradeService {
    Long requestTradeAsBuyer(TradeRequest tradeRequest, String username);
    Long requestTradeAsSeller(TradeRequest tradeRequest, String username);
    void acceptTrade(AcceptTradeRequest acceptTradeRequest, String username);
}
