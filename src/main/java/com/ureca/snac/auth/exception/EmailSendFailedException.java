package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class EmailSendFailedException extends BusinessException {
    public EmailSendFailedException() {
        super(BaseCode.EMAIL_SEND_FAILED);
    }
}