package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class PhoneNotVerifiedException extends BusinessException {
    public PhoneNotVerifiedException() {
        super(BaseCode.PHONE_NOT_VERIFIED);
    }
}