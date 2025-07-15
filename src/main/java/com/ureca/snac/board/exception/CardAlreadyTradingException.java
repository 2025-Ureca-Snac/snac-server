package com.ureca.snac.board.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.*;

public class CardAlreadyTradingException extends BusinessException {
    public CardAlreadyTradingException() {
        super(CARD_ALREADY_TRADING);
    }
}
