package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_PAYMENT_MISMATCH;

public class TradePaymentMismatchException extends BusinessException {
    public TradePaymentMismatchException() {
        super(TRADE_PAYMENT_MISMATCH);
    }
}
