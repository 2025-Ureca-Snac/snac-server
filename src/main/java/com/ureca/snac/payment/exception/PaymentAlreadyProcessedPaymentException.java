package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_ALREADY_PROCESSED_PAYMENT;

public class PaymentAlreadyProcessedPaymentException extends BusinessException {
    public PaymentAlreadyProcessedPaymentException() {
        super(PAYMENT_ALREADY_PROCESSED_PAYMENT);
    }
}
