package com.ureca.snac.board.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class CardInvalidStatusException extends BusinessException {
    public CardInvalidStatusException() {
        super(BaseCode.CARD_INVALID_STATUS);
    }
}
