package com.ureca.snac.member.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class InvalidCurrentPasswordException extends BusinessException {
    public InvalidCurrentPasswordException() {
        super(BaseCode.INVALID_CURRENT_PASSWORD);
    }
}
