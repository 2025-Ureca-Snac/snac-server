package com.ureca.snac.board.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.CARD_ALREADY_SOLD_OUT;

public class CardAlreadySoldOutException extends BusinessException {
    public CardAlreadySoldOutException() {
        super(CARD_ALREADY_SOLD_OUT);
    }
}
