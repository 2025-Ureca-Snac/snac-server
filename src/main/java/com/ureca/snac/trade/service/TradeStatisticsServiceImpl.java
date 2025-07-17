package com.ureca.snac.trade.service;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.TradeStatistics;
import com.ureca.snac.trade.exception.TradeStatisticsNotFoundException;
import com.ureca.snac.trade.repository.TradeStatisticsRepository;
import com.ureca.snac.trade.service.response.TradeStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeStatisticsServiceImpl implements TradeStatisticsService {

    private final TradeStatisticsRepository tradeStatisticsRepository;

    @Override
    public TradeStatisticsResponse getLatestStatsByCarrier(Carrier carrier) {
        TradeStatistics stat = tradeStatisticsRepository
                .findFirstByCarrierOrderByIdDesc(carrier)
                .orElseThrow(TradeStatisticsNotFoundException::new);

        return new TradeStatisticsResponse(stat.getCarrier(), stat.getAvgTotalPrice());
    }
}
