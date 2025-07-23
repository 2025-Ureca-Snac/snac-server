package com.ureca.snac.board.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.NOT_REALTIME_SELL_CARD;

public class NotRealTimeSellCardException extends BusinessException {

    public NotRealTimeSellCardException() {
        super(NOT_REALTIME_SELL_CARD);
    }
}
