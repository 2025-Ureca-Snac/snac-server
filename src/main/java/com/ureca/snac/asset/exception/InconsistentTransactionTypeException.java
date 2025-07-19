package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INCONSISTENT_TRANSACTION_TYPE;

public class InconsistentTransactionTypeException extends BusinessException {
    public InconsistentTransactionTypeException() {
        super(INCONSISTENT_TRANSACTION_TYPE);
    }
}
