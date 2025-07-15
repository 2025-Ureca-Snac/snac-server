package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.*;

public class TradeConfirmPermissionDeniedException extends BusinessException {
    public TradeConfirmPermissionDeniedException() {
        super(TRADE_CONFIRM_PERMISSION_DENIED);
    }
}
