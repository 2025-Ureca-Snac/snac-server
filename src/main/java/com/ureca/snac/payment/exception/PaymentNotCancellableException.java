package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_NOT_CANCELLABLE;

public class PaymentNotCancellableException extends BusinessException {
    public PaymentNotCancellableException() {
        super(PAYMENT_NOT_CANCELLABLE);
    }
}
