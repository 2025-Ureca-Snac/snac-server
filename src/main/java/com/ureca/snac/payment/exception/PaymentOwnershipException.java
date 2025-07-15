package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_OWNERSHIP_MISMATCH;

public class PaymentOwnershipException extends BusinessException {
    public PaymentOwnershipException() {
        super(PAYMENT_OWNERSHIP_MISMATCH);
    }
}
