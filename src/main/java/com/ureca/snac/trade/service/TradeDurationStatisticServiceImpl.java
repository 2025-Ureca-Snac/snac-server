package com.ureca.snac.trade.service;

import com.ureca.snac.trade.entity.TradeDurationStatistic;
import com.ureca.snac.trade.exception.TradeDurationStatisticNotFoundException;
import com.ureca.snac.trade.repository.TradeDurationStatisticRepository;
import com.ureca.snac.trade.service.interfaces.TradeDurationStatisticService;
import com.ureca.snac.trade.service.response.TradeDurationStatisticResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TradeDurationStatisticServiceImpl implements TradeDurationStatisticService {

    private final TradeDurationStatisticRepository tradeDurationStatisticRepository;

    @Override
    public TradeDurationStatisticResponse getLatestStatistic() {
        TradeDurationStatistic stat = tradeDurationStatisticRepository
                .findTopByOrderByCreatedAtDesc()
                .orElseThrow(TradeDurationStatisticNotFoundException::new);

        log.info("조회된 최신 거래 소요 시간 통계 - duration: {}초, recordedAt: {}", stat.getDurationSeconds(), stat.getCreatedAt());

        return new TradeDurationStatisticResponse(stat.getDurationSeconds());
    }
}
