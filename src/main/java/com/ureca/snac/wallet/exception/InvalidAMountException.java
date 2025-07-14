package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_AMOUNT;

public class InvalidAMountException extends BusinessException {
    public InvalidAMountException() {
        super(INVALID_AMOUNT);
    }
}