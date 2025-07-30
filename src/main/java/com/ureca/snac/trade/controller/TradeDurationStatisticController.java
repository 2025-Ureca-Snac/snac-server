package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.service.interfaces.TradeDurationStatisticService;
import com.ureca.snac.trade.service.response.TradeDurationStatisticResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TradeDurationStatisticController implements TradeDurationStatisticControllerSwagger{

    private final TradeDurationStatisticService tradeDurationStatisticService;

    @GetMapping("/api/trade-duration-statistics")
    public ResponseEntity<ApiResponse<TradeDurationStatisticResponse>> getLatestStatistic() {
        return ResponseEntity.ok(ApiResponse.of(BaseCode.TRADE_DURATION_STATISTIC_READ_SUCCESS, tradeDurationStatisticService.getLatestStatistic()));
    }
}
