package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class RefreshTokenException extends BusinessException {
    public RefreshTokenException(BaseCode baseCode) {
        super(baseCode);
    }
}