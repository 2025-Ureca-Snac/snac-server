package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.*;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super(INSUFFICIENT_BALANCE);
    }
}