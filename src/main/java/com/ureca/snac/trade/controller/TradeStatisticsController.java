package com.ureca.snac.trade.controller;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.trade.service.TradeStatisticsService;
import com.ureca.snac.trade.service.response.TradeStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.TRADE_STATISTICS_READ_SUCCESS;

@RestController
@RequestMapping("/api/trade-statistics")
@RequiredArgsConstructor
public class TradeStatisticsController {

    private final TradeStatisticsService tradeStatisticsService;

    @GetMapping
    public ResponseEntity<ApiResponse<TradeStatisticsResponse>> getLatest(@RequestParam Carrier carrier) {

        return ResponseEntity.ok(ApiResponse.of(TRADE_STATISTICS_READ_SUCCESS,
                tradeStatisticsService.getLatestStatsByCarrier(carrier)));
    }
}
