package com.ureca.snac.trade.exception;


import com.ureca.snac.common.exception.BusinessException;
import static com.ureca.snac.common.BaseCode.TRADE_CANCEL_NOT_FOUND;

public class TradeCancelNotFoundException extends BusinessException {
    public TradeCancelNotFoundException() {
        super(TRADE_CANCEL_NOT_FOUND);
    }
}