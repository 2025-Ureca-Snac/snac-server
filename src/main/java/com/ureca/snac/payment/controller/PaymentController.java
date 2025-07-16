package com.ureca.snac.payment.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.payment.dto.PaymentCancelRequest;
import com.ureca.snac.payment.service.PaymentService;
import com.ureca.snac.swagger.annotation.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.PAYMENT_CANCEL_SUCCESS;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentSwagger {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
            String paymentKey,
            PaymentCancelRequest request,
            @UserInfo CustomUserDetails userDetails) {
        paymentService.cancelPayment(paymentKey, request.reason(), userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(PAYMENT_CANCEL_SUCCESS));
    }
}
