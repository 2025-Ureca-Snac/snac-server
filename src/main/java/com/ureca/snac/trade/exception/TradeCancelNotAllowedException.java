package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class TradeCancelNotAllowedException extends BusinessException {
    public TradeCancelNotAllowedException() {
        super(BaseCode.TRADE_CANCEL_NOT_ALLOWED);
    }
}
