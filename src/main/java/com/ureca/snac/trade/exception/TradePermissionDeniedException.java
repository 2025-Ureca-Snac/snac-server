package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_PERMISSION_DENIED;

public class TradePermissionDeniedException extends BusinessException {
    public TradePermissionDeniedException() {
        super(TRADE_PERMISSION_DENIED);
    }
}