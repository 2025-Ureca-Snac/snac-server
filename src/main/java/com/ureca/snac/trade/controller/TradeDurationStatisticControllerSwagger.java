package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import com.ureca.snac.trade.service.response.TradeDurationStatisticResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "거래 소요 시간 통계", description = "실시간 매칭 거래 소요 시간(평균 등) 통계 조회 기능")
public interface TradeDurationStatisticControllerSwagger {

    @Operation(
            summary = "가장 최근 거래 소요 시간 통계 조회",
            description = """
            가장 최근에 저장된 실시간 매칭 거래의 평균 소요 시간 통계 데이터를 조회합니다.
        """
    )
    @ApiSuccessResponse(description = "거래 소요 시간 통계 데이터 조회 성공")
    @ErrorCode404(description = "조회 실패 - 통계 데이터 없음")
    @GetMapping("/api/trade-duration-statistics")
    ResponseEntity<ApiResponse<TradeDurationStatisticResponse>> getLatestStatistic();
}
