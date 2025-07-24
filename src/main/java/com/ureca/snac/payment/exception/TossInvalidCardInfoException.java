package com.ureca.snac.payment.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TOSS_INVALID_CARD_INFO;

public class TossInvalidCardInfoException extends BusinessException {
    public TossInvalidCardInfoException() {
        super(TOSS_INVALID_CARD_INFO);
    }
}
