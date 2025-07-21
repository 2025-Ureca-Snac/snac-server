package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ALREADY_PROCESSED_PAYMENT;

public class AlreadyProcessedPaymentException extends BusinessException {
    public AlreadyProcessedPaymentException() {
        super(ALREADY_PROCESSED_PAYMENT);
    }
}
