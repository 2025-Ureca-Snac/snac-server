package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class SocialUnlinkException extends BusinessException {
    public SocialUnlinkException(BaseCode code) { super(code); }
    public SocialUnlinkException(BaseCode code, String msg) { super(code, msg); }
}
