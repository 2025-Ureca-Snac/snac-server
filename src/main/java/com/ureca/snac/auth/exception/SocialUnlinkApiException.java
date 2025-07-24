package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.ExternalApiException;

public class SocialUnlinkApiException extends ExternalApiException {
    public SocialUnlinkApiException(BaseCode code) { super(code); }
    public SocialUnlinkApiException(BaseCode code, String msg) { super(code, msg); }
}
