package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_DURATION_STATISTIC_NOT_FOUND;

public class TradeDurationStatisticNotFoundException extends BusinessException {
    public TradeDurationStatisticNotFoundException() {
        super(TRADE_DURATION_STATISTIC_NOT_FOUND);
    }
}
