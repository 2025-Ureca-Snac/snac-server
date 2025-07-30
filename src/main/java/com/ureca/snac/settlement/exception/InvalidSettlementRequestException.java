package com.ureca.snac.settlement.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_SETTLEMENT_REQUEST;

public class InvalidSettlementRequestException extends BusinessException {
    public InvalidSettlementRequestException() {
        super(INVALID_SETTLEMENT_REQUEST);
    }
}
