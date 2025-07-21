package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_NOT_FOUND;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException() {
        super(PAYMENT_NOT_FOUND);
    }
}
