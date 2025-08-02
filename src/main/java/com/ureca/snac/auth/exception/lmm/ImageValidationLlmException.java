package com.ureca.snac.auth.exception.lmm;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.ExternalApiException;

public class ImageValidationLlmException extends ExternalApiException {


    public ImageValidationLlmException(BaseCode baseCode, String customMessage, Throwable cause) {
        super(baseCode, customMessage);
        initCause(cause);
    }
}
