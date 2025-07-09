package com.ureca.snac.board.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.CARD_NOT_FOUND;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException() {
        super(CARD_NOT_FOUND);
    }
}
