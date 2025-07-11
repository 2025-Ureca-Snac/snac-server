package com.ureca.snac.trade.service;

import com.ureca.snac.trade.dto.TradeRequest;
import com.ureca.snac.trade.dto.TradeResponse;

public interface TradeService {
    TradeResponse requestTrade(TradeRequest dto, Long requesterId);
    TradeResponse requestTradeByEmail(TradeRequest dto, String email);
    void acceptTrade(Long tradeId, Long accepterId);
    void acceptTradeByEmail(Long tradeId, String email);
}