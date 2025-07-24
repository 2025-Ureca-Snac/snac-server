package com.ureca.snac.money.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.money.dto.MoneyRechargePreparedResponse;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeSuccessResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiRechargeSuccessResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "스낵 머니 충전",
        description = "스낵 머니 충전과 관련된 API")
public interface MoneyRechargeSwagger {

    @Operation(summary = "스낵 머니 충전 요청",
            description = "사용자가 스낵 머니 충전을 요청하면, 외부 결제 토스를 위한 준비 정보 반환")
    @SecurityRequirement(name = "Authorization")
    @ApiRechargeSuccessResponse(description = "결제 준비 성공")
    @ErrorCode400(description = "충전 금액은 1000보다 커야한다")
    @ErrorCode401(description = "인증에 실패했습니다 유효하지않거나 만료된 토큰")
    @ErrorCode404(description = "사용자를 찾을 수 없다")
    @PostMapping("/api/money/recharge/prepare")
    ResponseEntity<ApiResponse<MoneyRechargePreparedResponse>> prepareRecharge(
            @Valid @RequestBody MoneyRechargeRequest request,
            @UserInfo CustomUserDetails userDetails
    );

    @Operation(
            summary = "토스 결제 성공 처리",
            description = "토스 페이먼츠에서 결제가 성공했을 때 호출되는 엔트포인트" +
                    "최종 결제를 승인하고 성공결과 JSON 반환"
    )
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "머니 충전 성공")
    @ErrorCode400(description = "요청 금액과 실제 결제 금액이 일치하지 않는 실패")
    @ErrorCode401(description = "인증에 실패했습니다 유효하지않거나 만료된 토큰")
    @ErrorCode404(description = "요청한 리소스를 찾을 수 없습니다")
    @GetMapping("/api/money/recharge/success")
    ResponseEntity<ApiResponse<MoneyRechargeSuccessResponse>> rechargeSuccess(
            @Parameter(description = "토스페이먼츠 결제 고유 키", required = true)
            @RequestParam String paymentKey,
            @Parameter(description = "우리 시스템의 고유 주문번호", required = true)
            @RequestParam String orderId,
            @Parameter(description = "실제 결제된 금액", required = true)
            @RequestParam Long amount,
            @UserInfo CustomUserDetails userDetails
    );
}
