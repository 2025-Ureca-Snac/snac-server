package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.ExternalApiException;

import static com.ureca.snac.common.BaseCode.TOSS_API_CALL_ERROR;

public class TossPaymentsAPiCallException extends ExternalApiException {
    public TossPaymentsAPiCallException() {
        super(TOSS_API_CALL_ERROR);
    }

    public TossPaymentsAPiCallException(String customMessage) {
        super(TOSS_API_CALL_ERROR, customMessage);
    }
}
