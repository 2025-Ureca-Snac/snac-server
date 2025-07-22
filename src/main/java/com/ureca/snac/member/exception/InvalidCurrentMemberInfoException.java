package com.ureca.snac.member.exception;

import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.common.BaseCode;

public class InvalidCurrentMemberInfoException extends BusinessException {
    public InvalidCurrentMemberInfoException(BaseCode code) {
        super(code);
    }
}