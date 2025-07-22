package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_ALREADY_CANCEL_REQUESTED;

public class TradeAlreadyCancelRequestedException extends BusinessException {
    public TradeAlreadyCancelRequestedException() {
        super(TRADE_ALREADY_CANCEL_REQUESTED);
    }
}