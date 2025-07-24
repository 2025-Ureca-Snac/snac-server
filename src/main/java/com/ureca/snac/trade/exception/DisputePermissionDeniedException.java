package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.DISPUTE_PERMISSION_DENIED;

public class DisputePermissionDeniedException extends BusinessException {
    public DisputePermissionDeniedException() {
        super(DISPUTE_PERMISSION_DENIED);
    }
}

