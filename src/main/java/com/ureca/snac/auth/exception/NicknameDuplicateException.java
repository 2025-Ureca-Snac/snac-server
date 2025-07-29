package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class NicknameDuplicateException extends BusinessException {
    public NicknameDuplicateException() {
        super(BaseCode.NICKNAME_DUPLICATE);
    }
}