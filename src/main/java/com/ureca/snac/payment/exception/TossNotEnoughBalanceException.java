package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TOSS_NOT_ENOUGH_BALANCE;

public class TossNotEnoughBalanceException extends BusinessException {
    public TossNotEnoughBalanceException() {
        super(TOSS_NOT_ENOUGH_BALANCE);
    }
}
