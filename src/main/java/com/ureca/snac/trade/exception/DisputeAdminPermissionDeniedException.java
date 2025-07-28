package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.DISPUTE_ADMIN_PERMISSION_DENIED;


public class DisputeAdminPermissionDeniedException extends BusinessException {
    public DisputeAdminPermissionDeniedException() {
        super(DISPUTE_ADMIN_PERMISSION_DENIED);
    }
}

