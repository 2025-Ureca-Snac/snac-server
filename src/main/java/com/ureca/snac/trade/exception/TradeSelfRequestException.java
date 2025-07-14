package com.ureca.snac.trade.exception;
import com.ureca.snac.common.exception.BusinessException;
import static com.ureca.snac.common.BaseCode.TRADE_SELF_REQUEST;
public class TradeSelfRequestException extends BusinessException {
    public TradeSelfRequestException() { super(TRADE_SELF_REQUEST); }
}