package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class SocialTokenException extends BusinessException {
    public SocialTokenException(BaseCode baseCode) {
        super(baseCode);
    }
}
