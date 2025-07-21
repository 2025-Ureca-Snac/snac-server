package com.ureca.snac.auth.service;

import com.ureca.snac.trade.dto.TradeMessageDto;

public interface SmsListenerService {
    void sendSms(TradeMessageDto tradeMessageDto);
}
