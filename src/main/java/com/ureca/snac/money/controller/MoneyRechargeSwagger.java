package com.ureca.snac.money.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiRechargeSuccessResponse;
import com.ureca.snac.swagger.annotation.response.ApiRedirectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "스낵 머니 충전",
        description = "스낵 머니 충전과 관련된 API")
public interface MoneyRechargeSwagger {

    @Operation(summary = "스낵 머니 충전 요청",
            description = "사용자가 스낵 머니 충전을 요청하면, 결제를 위한 사전 준비 정보 반환")
    @SecurityRequirement(name = "Authorization")
    @ApiRechargeSuccessResponse(description = "성공")
    @ErrorCode400(description = "충전 금액은 1000보다 커야한다")
    @ErrorCode401
    @PostMapping("/api/money/recharge/prepare")
    ResponseEntity<ApiResponse<MoneyRechargeResponse>> prepareRecharge(
            @Valid @RequestBody MoneyRechargeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
    // 성공 응답은 DTO 로 명시, 에러 응답은 어노테이션으로 처리

    @Operation(
            summary = "토스 결제 성공 콜백", description = "토스 페이먼츠에서 결제가 성공했을 때 호출되는 엔트포인트"
    )
    @ApiRedirectResponse(description = "결제 성공 페이지로 리다이렉트")
    @ErrorCode400(description = "요청 금액과 실제 결제 금액이 일치하지 않는 실패")
    @ErrorCode404
    @GetMapping("/api/money/recharge/success")
    ResponseEntity<Void> rechargeSuccess(
            @Parameter(description = "토스페이먼츠 결제 고유 키", required = true) @RequestParam String paymentKey,
            @Parameter(description = "우리 시스템의 고유 주문번호", required = true) @RequestParam String orderId,
            @Parameter(description = "실제 결제된 금액", required = true) @RequestParam Long amount
    );
    // 리다이렉션으로 응답, 특정 에러는 ApiResponse로 설명 구체화, 공통 에러는 404
}
