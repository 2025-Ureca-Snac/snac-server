package com.ureca.snac.finance.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ACCOUNT_NOT_FOUND;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(ACCOUNT_NOT_FOUND);
    }
}
