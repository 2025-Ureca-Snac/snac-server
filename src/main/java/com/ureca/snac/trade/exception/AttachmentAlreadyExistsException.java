package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class AttachmentAlreadyExistsException extends BusinessException {
    public AttachmentAlreadyExistsException() {
        super(BaseCode.ATTACHMENT_ALREADY_EXISTS);
    }
}