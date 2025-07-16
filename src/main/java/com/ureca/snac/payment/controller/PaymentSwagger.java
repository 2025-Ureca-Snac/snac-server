package com.ureca.snac.payment.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.payment.dto.PaymentCancelRequest;
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

@Tag(name = "결제 API", description = "결제 취소 등 결제 관련")
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
    ResponseEntity<ApiResponse<Void>> cancelPayment(
            @PathVariable String paymentKey,
            @Valid @RequestBody PaymentCancelRequest request,
            @UserInfo CustomUserDetails userDetails
    );
}
