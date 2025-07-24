package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.DISPUTE_NOT_FOUND;

public class DisputeNotFoundException extends BusinessException {
    public DisputeNotFoundException() {
        super(DISPUTE_NOT_FOUND);
    }
}