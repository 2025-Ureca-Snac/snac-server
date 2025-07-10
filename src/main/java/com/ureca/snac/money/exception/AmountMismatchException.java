package com.ureca.snac.money.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.AMOUNT_MISMATCH;

public class AmountMismatchException extends BusinessException {
    public AmountMismatchException() {
        super(AMOUNT_MISMATCH);
    }
}
