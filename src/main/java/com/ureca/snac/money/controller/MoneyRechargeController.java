package com.ureca.snac.money.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.money.dto.request.MoneyRechargeRequest;
import com.ureca.snac.money.dto.response.MoneyRechargeResponse;
import com.ureca.snac.money.service.MoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static com.ureca.snac.common.BaseCode.MONEY_RECHARGE_PREPARE_SUCCESS;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class MoneyRechargeController implements MoneyRechargeSwagger {

    private final MoneyService moneyService;

    @Value("${payments.toss.success-url}")
    private String successUrl;

    @Override
    public ResponseEntity<ApiResponse<MoneyRechargeResponse>> prepareRecharge(
            @RequestBody MoneyRechargeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MoneyRechargeResponse response = moneyService.prepareRecharge(request, userDetails.getMember());

        return ResponseEntity.ok(ApiResponse.of(MONEY_RECHARGE_PREPARE_SUCCESS, response));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> rechargeSuccess(
            String paymentKey, String orderId, Long amount) {
        moneyService.processRechargeSuccess(paymentKey, orderId, amount);

        URI redirectUri;

        try {
            redirectUri = new URI(successUrl + "?orderId=" + orderId);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return new ResponseEntity<>(headers, FOUND);
    }
}
