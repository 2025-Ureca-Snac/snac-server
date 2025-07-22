package com.ureca.snac.member.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class NicknameChangeTooEarlyException extends BusinessException {
    public NicknameChangeTooEarlyException() {
        super(BaseCode.NICKNAME_CHANGE_TOO_EARLY);
    }
}
