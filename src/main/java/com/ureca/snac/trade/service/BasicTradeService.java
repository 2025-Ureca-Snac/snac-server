package com.ureca.snac.trade.service;

import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BasicTradeService {
    Long createSellTrade(CreateTradeRequest createTradeRequest, String username);
    Long createBuyTrade(CreateTradeRequest createTradeRequest, String username);
    void cancelTrade(Long tradeId, String username);
    void sendTradeData(Long tradeId, String username, MultipartFile picture);
    void confirmTrade(Long tradeId, String username);
    ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, Long lastTradeId);
}
