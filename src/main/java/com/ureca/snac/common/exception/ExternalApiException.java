package com.ureca.snac.common.exception;

import com.ureca.snac.common.BaseCode;
import lombok.Getter;

@Getter
public class ExternalApiException extends BaseCustomException {

    public ExternalApiException(BaseCode baseCode) {
        super(baseCode);
    }

    public ExternalApiException(BaseCode baseCode, String customMessage) {
        super(baseCode, customMessage);
    }
}
