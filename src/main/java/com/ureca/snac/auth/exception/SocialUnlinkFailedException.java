package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class SocialUnlinkFailedException extends BusinessException {
        public SocialUnlinkFailedException(BaseCode code) { super(code); }
}
