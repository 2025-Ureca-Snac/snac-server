package com.ureca.snac.trade.scheduler;

import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeDurationStatistic;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.entity.TradeType;
import com.ureca.snac.trade.repository.TradeDurationStatisticRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.Duration.between;

@Slf4j
@Component
@Profile("scheduler")
@RequiredArgsConstructor
public class TradeDurationScheduler {

    private final TradeRepository tradeRepository;
    private final TradeDurationStatisticRepository tradeDurationStatisticRepository;

    @Scheduled(cron = "0 0 * * * *")
//    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(
            name = "recordHourlyAverageDuration",
            lockAtMostFor = "PT59M",
            lockAtLeastFor = "PT1M"
    )
    public void recordHourlyAverageDuration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(24);

        List<Trade> trades = tradeRepository
                .findByTradeTypeAndStatusAndUpdatedAtBetween(
                        TradeType.REALTIME,
                        TradeStatus.COMPLETED,
                        since,
                        now
                );

        if (trades.isEmpty()) {
            log.info("지난 24시간 동안 COMPLETED 상태의 REALTIME 거래가 없어 평균 소요 시간 계산을 건너뜁니다.");
            return;
        }

        // 총 소요 시간 (초) 합산
        long sumSeconds = trades.stream()
                .mapToLong(trade -> between(trade.getCreatedAt(), trade.getUpdatedAt()).getSeconds())
                .sum();

        long avgSeconds = (long) (sumSeconds / trades.size()); // 평균 소유 시간 (초, 소수점 절삭)

        log.info("지난 24시간 REALTIME 거래 건수: {}, 평균 소요 시간: {}초", trades.size(), avgSeconds);

        // 저장
        TradeDurationStatistic stat = TradeDurationStatistic.builder()
                .durationSeconds(avgSeconds)
                .build();

        tradeDurationStatisticRepository.save(stat);

        log.info("평균 소요 시간을 trade_duration_statistic 테이블에 저장했습니다. (평균: {}초, 저장 시각: {})", avgSeconds, now);
    }
}
