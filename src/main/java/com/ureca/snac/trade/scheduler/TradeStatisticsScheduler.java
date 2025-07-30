package com.ureca.snac.trade.scheduler;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatistics;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.repository.TradeStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeStatisticsScheduler {

    private final TradeRepository tradeRepository;
    private final TradeStatisticsRepository tradeStatisticsRepository;

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(
            name = "recordHourlyAverageByCarrier",
            lockAtMostFor = "PT59M",
            lockAtLeastFor = "PT1M"
    )
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
            int totalCost = 0;

            for (Trade trade : trades) {
                totalCost += trade.getPriceGb();
                totalDataAmount += trade.getDataAmount();
            }

            double avgPricePerGb = totalDataAmount == 0 ? 0.0 : (double) totalCost / totalDataAmount;

            log.info("carrier={} | avgPricePerGb={}", carrier, avgPricePerGb);

            TradeStatistics stat = TradeStatistics.builder()
                    .carrier(carrier)
                    .avgTotalPrice(avgPricePerGb)
                    .build();

            tradeStatisticsRepository.save(stat);
        }

        log.info("Finished hourly averaging job at {}", now);
    }
}
