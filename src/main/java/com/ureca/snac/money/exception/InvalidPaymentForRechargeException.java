package com.ureca.snac.money.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_PAYMENT_FOR_RECHARGE;

public class InvalidPaymentForRechargeException extends BusinessException {
    public InvalidPaymentForRechargeException() {
        super(INVALID_PAYMENT_FOR_RECHARGE);
    }
}
