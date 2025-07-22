package com.ureca.snac.notification.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.trade.dto.TradeDto;

public interface NotificationService {
    void notify(String username, TradeDto tradeDto);
    void sendMatchingNotification(String username, CardDto cardDto);
}
