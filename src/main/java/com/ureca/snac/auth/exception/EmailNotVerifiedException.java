package com.ureca.snac.auth.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.EMAIL_NOT_VERIFIED;
import static com.ureca.snac.common.BaseCode.EMAIL_SEND_FAILED;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super(EMAIL_NOT_VERIFIED);
    }
}