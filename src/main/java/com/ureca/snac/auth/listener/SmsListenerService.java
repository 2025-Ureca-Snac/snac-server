package com.ureca.snac.auth.listener;

import com.ureca.snac.trade.dto.TradeMessageDto;

public interface SmsListenerService {
    void sendSms(TradeMessageDto tradeMessageDto);
}
