package com.ureca.snac.settlement.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.SETTLEMENT_ACCOUNT_MISMATCH;

public class SettlementAccountMismatchException extends BusinessException {
    public SettlementAccountMismatchException() {
        super(SETTLEMENT_ACCOUNT_MISMATCH);
    }
}
