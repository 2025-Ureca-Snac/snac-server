package com.ureca.snac.finance.exception;

import com.ureca.snac.common.exception.BusinessException;
import lombok.Getter;

import static com.ureca.snac.common.BaseCode.BANK_NOT_FOUND;

@Getter
public class BankNotFoundException extends BusinessException {

    public BankNotFoundException() {
        super(BANK_NOT_FOUND);
    }
}
