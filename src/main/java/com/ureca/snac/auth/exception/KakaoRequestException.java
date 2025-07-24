package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class KakaoRequestException extends BusinessException {
    public KakaoRequestException(BaseCode baseCode) {
        super(baseCode);
    }
}
