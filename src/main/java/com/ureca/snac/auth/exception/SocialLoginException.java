package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class SocialLoginException extends BusinessException {
    public SocialLoginException(BaseCode baseCode) {
        super(baseCode);
    }
}
