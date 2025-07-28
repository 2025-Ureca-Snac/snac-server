package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_SEND_PERMISSION_DENIED;

public class TradeSendPermissionDeniedException extends BusinessException {
    public TradeSendPermissionDeniedException() {
        super(TRADE_SEND_PERMISSION_DENIED);
    }
}

