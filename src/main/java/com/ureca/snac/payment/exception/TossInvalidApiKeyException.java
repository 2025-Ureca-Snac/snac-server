package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.ExternalApiException;

import static com.ureca.snac.common.BaseCode.TOSS_INVALID_API_KEY;

public class TossInvalidApiKeyException extends ExternalApiException {
    public TossInvalidApiKeyException() {
        super(TOSS_INVALID_API_KEY);
    }
}
