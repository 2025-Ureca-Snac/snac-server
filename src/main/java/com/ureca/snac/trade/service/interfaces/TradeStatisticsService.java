package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.service.response.TradeStatisticsResponse;

public interface TradeStatisticsService {
    TradeStatisticsResponse getLatestStatsByCarrier(Carrier carrier);
}
