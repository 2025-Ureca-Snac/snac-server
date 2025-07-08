package com.ureca.snac.finance.exception;

import com.ureca.snac.common.BaseCode;
import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public class BankNotFoundException extends NoSuchElementException {

    private final BaseCode baseCode;

    public BankNotFoundException(BaseCode baseCode) {
        super(baseCode.getMessage());
        this.baseCode = baseCode;
    }
}
