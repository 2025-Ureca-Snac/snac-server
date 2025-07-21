package com.ureca.snac.board.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.CARD_ALREADY_SELLING;

public class CardAlreadySellingException extends BusinessException {
    public CardAlreadySellingException() {
        super(CARD_ALREADY_SELLING);
    }
}
