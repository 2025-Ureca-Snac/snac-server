package com.ureca.snac.notification.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.trade.dto.CancelTradeDto;
import com.ureca.snac.trade.dto.RetrieveFilterDto;
import com.ureca.snac.trade.dto.TradeDto;

public interface NotificationService {
    void notify(String username, TradeDto tradeDto);
    void sendMatchingNotification(String username, CardDto cardDto);
    void sendBuyFilterNotification(RetrieveFilterDto dto);
    void sendCancelNotification(CancelTradeDto dto);
}
