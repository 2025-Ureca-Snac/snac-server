package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class TradeCancelPermissionDeniedException extends BusinessException {
    public TradeCancelPermissionDeniedException() {
        super(BaseCode.TRADE_CANCEL_PERMISSION_DENIED);
    }
}
