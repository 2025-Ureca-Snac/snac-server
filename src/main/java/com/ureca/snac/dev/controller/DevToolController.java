package com.ureca.snac.dev.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.dev.dto.DevCancelRechargeRequest;
import com.ureca.snac.dev.dto.DevForceTradeCompleteRequest;
import com.ureca.snac.dev.dto.DevPointGrantRequest;
import com.ureca.snac.dev.dto.DevRechargeRequest;
import com.ureca.snac.dev.service.DevToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Tag(name = "더미데이터 생성", description = "강제로 충전, 충전 취소, 거래 등 지원")
public class DevToolController {

    private final DevToolService devToolService;

    @Operation(summary = "개발용 강제 머니 충전")
    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<Long>> forceRecharge(
            @RequestBody DevRechargeRequest request) {
        log.info("[개발용 충전] 시작. email : {}, amount : {}",
                request.email(), request.amount());
        Long paymentId = devToolService.forceRecharge(request);

        log.info("[개발용 충전] 완료. Payment Id : {}", paymentId);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, paymentId));
    }

    @Operation(summary = "개발용 강제 포인트 적립")
    @PostMapping("/point/grant")
    public ResponseEntity<ApiResponse<Void>> grantPoint(
            @RequestBody DevPointGrantRequest request) {
        log.info("[개발용 포인트 지급] 시작. email : {}, amount : {}, reason : {}",
                request.email(), request.amount(), request.reason());

        devToolService.grantPoint(request);
        log.info("[개발용 포인트 지급] 완료");
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.STATUS_OK));
    }


    @Operation(summary = "개발용 강제 충전 취소")
    @PostMapping("/recharge/cancel")
    public ResponseEntity<ApiResponse<Void>> forceCancelRecharge(
            @RequestBody DevCancelRechargeRequest request) {
        log.info("[개발용 충전 취소] 시작. payment Id : {}, reason : {}",
                request.paymentId(), request.reason());
        devToolService.forceCancelRecharge(request);

        log.info("[개발용 충전 취소] 완료");
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.STATUS_OK));
    }

    @Operation(summary = "개발용 강제 거래 완료")
    @PostMapping("/trade/complete")
    public ResponseEntity<ApiResponse<Long>> forceTradeComplete(
            @RequestBody DevForceTradeCompleteRequest request) {
        log.info("[개발용 거래 완료] 시작. 작성자 : {}, 상대방 : {}, 종류 : {}, 통신사 : {}, 데이터용량 : {},머니 사용 : {}, 포인트 사용 : {}",
                request.cardOwnerEmail(), request.counterEmail(), request.cardCategory(), request.carrier(),
                request.dataAmount(), request.moneyAmountToUse(), request.pointAmountToUse());

        Long tradeId = devToolService.forceTradeComplete(request);
        log.info("[개발용 거래 완료] 완료 Trade Id :{}", tradeId);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, tradeId));
    }
}
