package com.ureca.snac.money.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;
import com.ureca.snac.money.service.MoneyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import static com.ureca.snac.common.BaseCode.MONEY_RECHARGE_PREPARE_SUCCESS;

@Slf4j
@RestController
public class MoneyRechargeController implements MoneyRechargeSwagger {

    private final MoneyService moneyService;
    private final String successUrl;

    public MoneyRechargeController(MoneyService moneyService,
                                   @Value("${payments.toss.success-url}") String successUrl) {
        this.moneyService = moneyService;
        this.successUrl = successUrl;
    }

    @Override
    public ResponseEntity<ApiResponse<MoneyRechargeResponse>> prepareRecharge(
            @Valid @RequestBody MoneyRechargeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MoneyRechargeResponse response = moneyService.prepareRecharge(request, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(MONEY_RECHARGE_PREPARE_SUCCESS, response));
    }

    @Override
    public RedirectView rechargeSuccess(
            String paymentKey, String orderId, Long amount, CustomUserDetails userDetails) {

        moneyService.processRechargeSuccess(paymentKey, orderId, amount, userDetails.getUsername());

        log.info("결제 성공 및 머니 충전 완료, 리다이렉트 실행. 목적지 : {}", successUrl);
        return new RedirectView(successUrl);
    }
}
