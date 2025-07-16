package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class AttachmentNotFoundException extends BusinessException {
    public AttachmentNotFoundException() {
        super(BaseCode.ATTACHMENT_NOT_FOUND);
    }
}