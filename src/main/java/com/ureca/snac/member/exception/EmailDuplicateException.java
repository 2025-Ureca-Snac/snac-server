package com.ureca.snac.member.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class EmailDuplicateException extends BusinessException {
    public EmailDuplicateException() {
        super(BaseCode.EMAIL_DUPLICATE);
    }
}