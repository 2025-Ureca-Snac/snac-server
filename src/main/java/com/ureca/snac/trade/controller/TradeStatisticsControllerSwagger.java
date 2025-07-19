package com.ureca.snac.trade.controller;


import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import com.ureca.snac.trade.service.response.TradeStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "거래 통계", description = "캐리어별 최근 24시간 거래 통계 조회")
public interface TradeStatisticsControllerSwagger {

    @Operation(
            summary = "캐리어별 최신 24시간 거래 통계 조회",
            description = """
            특정 캐리어(SK, KT, LG 등)의 가장 최근 저장된
            24시간 거래 통계를 조회합니다.
        """
    )
    @ApiSuccessResponse(description = "거래 통계 데이터 조회 성공")
    @ErrorCode400(description = "조회 실패 - 잘못된 요청 파라미터")
    @ErrorCode404(description = "조회 실패 - 해당 캐리어 통계 데이터 없음")
    @GetMapping
    ResponseEntity<ApiResponse<TradeStatisticsResponse>> getLatest(@RequestParam Carrier carrier);
}
