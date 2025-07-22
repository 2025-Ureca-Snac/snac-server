package com.ureca.snac.payment.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.payment.dto.PaymentCancelRequest;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.service.PaymentService;
import com.ureca.snac.swagger.annotation.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.PAYMENT_CANCEL_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentSwagger {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<ApiResponse<PaymentCancelResponse>> cancelPayment(
            @PathVariable String paymentKey,
            @Valid @RequestBody PaymentCancelRequest request,
            @UserInfo CustomUserDetails userDetails) {

        log.info("[결제 취소 요청] 시작. paymentKey : {}", paymentKey);

        PaymentCancelResponse cancelResponse = paymentService.cancelPayment(
                paymentKey, request.reason(), userDetails.getUsername()
        );
        log.info("[결제 취소 요청] 성공 paymentKey : {}", paymentKey);

        return ResponseEntity.ok(ApiResponse.of(PAYMENT_CANCEL_SUCCESS, cancelResponse));
    }
}
