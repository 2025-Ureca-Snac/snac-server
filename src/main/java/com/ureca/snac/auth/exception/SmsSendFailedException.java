package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class SmsSendFailedException extends BusinessException {
    public SmsSendFailedException() {
        super(BaseCode.SMS_SEND_FAILED);
    }
}