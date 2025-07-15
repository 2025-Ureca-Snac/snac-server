package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_INVALID_STATUS;

public class TradeInvalidStatusException extends BusinessException {
    public TradeInvalidStatusException() {
        super(TRADE_INVALID_STATUS);
    }
}
