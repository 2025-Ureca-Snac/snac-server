package com.ureca.snac.trade.service;

import com.ureca.snac.trade.controller.request.CreateTradeRequest;

public interface BasicTradeService {
    Long createSellTrade(CreateTradeRequest createTradeRequest, String username);
    Long createBuyTrade(CreateTradeRequest createTradeRequest, String username);
}
