package com.ureca.snac.money.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.money.dto.MoneyRechargePreparedResponse;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeSuccessResponse;
import com.ureca.snac.money.service.MoneyService;
import com.ureca.snac.swagger.annotation.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.MONEY_RECHARGE_PREPARE_SUCCESS;
import static com.ureca.snac.common.BaseCode.MONEY_RECHARGE_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MoneyRechargeController implements MoneyRechargeSwagger {

    private final MoneyService moneyService;

    @Override
    public ResponseEntity<ApiResponse<MoneyRechargePreparedResponse>> prepareRecharge(
            @Valid @RequestBody MoneyRechargeRequest request,
            @UserInfo CustomUserDetails userDetails) {
        MoneyRechargePreparedResponse response = moneyService.prepareRecharge(request, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(MONEY_RECHARGE_PREPARE_SUCCESS, response));
    }

    @Override
    public ResponseEntity<ApiResponse<MoneyRechargeSuccessResponse>> rechargeSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @UserInfo CustomUserDetails userDetails) {

        log.info("[결제 성공 처리] 시작. 주문번호 : {}", orderId);

        MoneyRechargeSuccessResponse successResponse =
                moneyService.processRechargeSuccess(
                        paymentKey, orderId, amount, userDetails.getUsername()
                );

        log.info("[결제 성공 처리] 완료, 응답 반환. 주문번호 : {}", orderId);

        return ResponseEntity.ok(ApiResponse.of(MONEY_RECHARGE_SUCCESS, successResponse));
    }
}
