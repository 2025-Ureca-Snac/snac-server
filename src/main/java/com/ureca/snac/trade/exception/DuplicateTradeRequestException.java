package com.ureca.snac.trade.exception;
import com.ureca.snac.common.exception.BusinessException;
import static com.ureca.snac.common.BaseCode.DUPLICATE_TRADE_REQUEST;
public class DuplicateTradeRequestException extends BusinessException {
    public DuplicateTradeRequestException() { super(DUPLICATE_TRADE_REQUEST); }
}