package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class TradeStatusMismatchException extends BusinessException {
    public TradeStatusMismatchException() {
        super(BaseCode.TRADE_STATUS_MISMATCH);
    }
}
