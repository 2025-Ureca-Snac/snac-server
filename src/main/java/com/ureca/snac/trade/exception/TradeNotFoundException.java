package com.ureca.snac.trade.exception;
import com.ureca.snac.common.exception.BusinessException;
import static com.ureca.snac.common.BaseCode.TRADE_NOT_FOUND;
public class TradeNotFoundException extends BusinessException {
    public TradeNotFoundException() { super(TRADE_NOT_FOUND); }
}