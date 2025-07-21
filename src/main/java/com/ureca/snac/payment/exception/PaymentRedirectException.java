package com.ureca.snac.payment.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BaseCustomException;

public class PaymentRedirectException extends BaseCustomException {

    public PaymentRedirectException(BaseCode baseCode) {
        super(baseCode);
    }
}
