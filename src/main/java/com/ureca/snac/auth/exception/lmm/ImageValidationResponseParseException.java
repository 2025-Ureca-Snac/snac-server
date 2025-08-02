package com.ureca.snac.auth.exception.lmm;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.ExternalApiException;

public class ImageValidationResponseParseException extends ExternalApiException {

    public ImageValidationResponseParseException(BaseCode baseCode, String customMessage) {
        super(baseCode, customMessage);
    }

    public ImageValidationResponseParseException(BaseCode baseCode, String customMessage, Throwable cause) {
        super(baseCode, customMessage);
        initCause(cause);
    }
}
