package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.PAYMENT_PERIOD_EXPIRED;
import static com.ureca.snac.common.BaseCode.TOSS_INVALID_CARD_INFO;

public class PaymentPeriodExpiredException extends BusinessException {
    public PaymentPeriodExpiredException() {
        super(PAYMENT_PERIOD_EXPIRED);
    }
}
