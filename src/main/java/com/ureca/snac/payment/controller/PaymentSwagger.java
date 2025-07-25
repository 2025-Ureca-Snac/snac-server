package com.ureca.snac.payment.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.payment.dto.PaymentCancelRequest;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.dto.PaymentFailureRequest;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.error.ErrorCode409;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "스낵 머니 충전 취소", description = "토스 페이먼츠 결제 취소 API")
@RequestMapping("/api/payments")
public interface PaymentSwagger {

    @Operation(summary = "결제 취소", description = "성공된 결제를 취소하고, 충전된 스낵머니를 환불 처리합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "결제 취소 성공")
    @ErrorCode401(description = "인증 실패, 유효하지않거나 만료된 토큰")
    @ErrorCode403(description = "해당 결제 취소할 권한이 없습니다")
    @ErrorCode404(description = "취소할 결제 정보를 찾을 수 없습니다")
    @ErrorCode409(description = "결제 취소할 수 없는 상태")

    @PostMapping("/{paymentKey}/cancel")
    ResponseEntity<ApiResponse<PaymentCancelResponse>> cancelPayment(
            @PathVariable String paymentKey,
            @Valid @RequestBody PaymentCancelRequest request,
            @UserInfo CustomUserDetails userDetails
    );

    @Operation(summary = "결제 실패 기록",
            description = "클라이언트가 결제 실패 후, 그 결과를 서버에 기록하기 위해 호출하는 API")
    @ApiSuccessResponse(description = "결제 실패 내역 기록 성공")
    @ErrorCode404(description = "기록할 결제 정보를 찾을 수 없습니다")
    @ErrorCode409(description = "이미 처리된 결제 입니다")
    @PostMapping("/fail")
    ResponseEntity<ApiResponse<Void>> handlePaymentFailure(
            @Valid @RequestBody PaymentFailureRequest request
    );
}
