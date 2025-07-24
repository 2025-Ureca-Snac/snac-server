package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ALREADY_USED_RECHARGE_CANNOT_CANCEL;

public class AlreadyUsedRechargeCannotCancelException extends BusinessException {
    public AlreadyUsedRechargeCannotCancelException() {
        super(ALREADY_USED_RECHARGE_CANNOT_CANCEL);
    }
}
