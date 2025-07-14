package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class VerificationFailedException extends BusinessException {
    public VerificationFailedException(BaseCode baseCode) {
        super(baseCode);
    }
}
