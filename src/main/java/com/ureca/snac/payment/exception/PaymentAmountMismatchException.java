package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_AMOUNT_MISMATCH;

public class PaymentAmountMismatchException extends BusinessException {
    public PaymentAmountMismatchException() {
        super(PAYMENT_AMOUNT_MISMATCH);
    }
}
