package com.ureca.snac.trade.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.TRADE_STATISTICS_NOT_FOUND;

public class TradeStatisticsNotFoundException extends BusinessException {
    public TradeStatisticsNotFoundException() {
        super(TRADE_STATISTICS_NOT_FOUND);
    }
}
