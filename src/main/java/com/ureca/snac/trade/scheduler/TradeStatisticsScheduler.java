package com.ureca.snac.trade.scheduler;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatistics;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.repository.TradeStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TradeStatisticsScheduler {

    private final TradeRepository tradeRepository;
    private final TradeStatisticsRepository tradeStatisticsRepository;

//    @Scheduled(cron = "0 0 * * * *")
    public void recordHourlyAverageByCarrier() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(24);

        for (Carrier carrier : Carrier.values()) {
            List<Trade> trades = tradeRepository
                    .findAllByStatusAndCarrierAndCreatedAtBetween(
                            TradeStatus.COMPLETED,
                            carrier,
                            since,
                            now
                    );

            int totalDataAmount = 0;
            int totalCost       = 0;

            for (Trade trade : trades) {
                totalCost       += trade.getPriceGb();
                totalDataAmount += trade.getDataAmount();
            }

            double avgPricePerGb = totalDataAmount == 0 ? 0.0 : (double) totalCost / totalDataAmount;

            TradeStatistics stat = TradeStatistics.builder()
                    .carrier(carrier)
                    .avgTotalPrice(avgPricePerGb)
                    .build();

            tradeStatisticsRepository.save(stat);

        }
    }
}
