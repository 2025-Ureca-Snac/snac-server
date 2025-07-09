package com.ureca.snac.money.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ALREADY_PROCESSED_ORDER;

public class AlreadyProcessedOrderException extends BusinessException {
    public AlreadyProcessedOrderException() {
        super(ALREADY_PROCESSED_ORDER);
    }
}
