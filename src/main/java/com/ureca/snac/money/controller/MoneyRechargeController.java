package com.ureca.snac.money.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.infra.config.TossPaymentProperties;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;
import com.ureca.snac.money.service.MoneyService;
import com.ureca.snac.swagger.annotation.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import static com.ureca.snac.common.BaseCode.MONEY_RECHARGE_PREPARE_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MoneyRechargeController implements MoneyRechargeSwagger {

    private final MoneyService moneyService;
    private final TossPaymentProperties tossPaymentProperties;

    @Override
    public ResponseEntity<ApiResponse<MoneyRechargeResponse>> prepareRecharge(
            @Valid @RequestBody MoneyRechargeRequest request,
            @UserInfo CustomUserDetails userDetails) {
        MoneyRechargeResponse response = moneyService.prepareRecharge(request, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(MONEY_RECHARGE_PREPARE_SUCCESS, response));
    }

    @Override
    public RedirectView rechargeSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @UserInfo CustomUserDetails userDetails) {

        moneyService.processRechargeSuccess(paymentKey, orderId, amount, userDetails.getUsername());

        String successUrl = tossPaymentProperties.getSuccessUrl();
        log.info("결제 성공 및 머니 충전 완료, 리다이렉트 실행. 목적지 : {}", successUrl);
        return new RedirectView(successUrl);
    }
}
