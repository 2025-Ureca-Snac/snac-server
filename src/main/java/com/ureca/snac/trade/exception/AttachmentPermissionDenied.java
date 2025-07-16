package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class AttachmentPermissionDenied extends BusinessException {
    public AttachmentPermissionDenied() {
        super(BaseCode.ATTACHMENT_PERMISSION_DENIED);
    }
}
