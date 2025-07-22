package com.ureca.snac.wallet.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.wallet.dto.WalletSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "내 지갑 관리",
        description = "지갑 잔액 조회 API")
@RequestMapping("/api/wallets")
public interface WalletSwagger {

    @Operation(summary = "내 지갑 요약 조회",
            description = "인증된 사용자의 스낵 머니 및 스낵 포인트 잔액을 조회한다. like 네이버페이")
    @SecurityRequirement(name = "Authorization")
    @ErrorCode401
    @ErrorCode404(description = "해당 사용자의 지갑 정보를 찾을 수 없습니다")
    @GetMapping("/summary")
    ResponseEntity<ApiResponse<WalletSummaryResponse>> getMyWalletSummary(
            @UserInfo CustomUserDetails userDetails
    );
}
