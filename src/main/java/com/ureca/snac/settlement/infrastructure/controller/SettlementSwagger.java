package com.ureca.snac.settlement.infrastructure.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.settlement.application.dto.SettlementRequest;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "정산 API",
        description = "머니 정산 관련 API")
public interface SettlementSwagger {

    @Operation(summary = "머니 정산 요청", description = "사용자의 스낵 머니를 등록된 계좌로 정산")
    @SecurityRequirement(name = "Authorization")
    @ErrorCode400(description = "계좌번호 불일치 또는 잘못된 요청 값입니다")
    @ErrorCode401
    @ErrorCode403
    @ErrorCode404
    ResponseEntity<ApiResponse<Void>> createSettlement(
            @UserInfo CustomUserDetails userDetails,
            @RequestBody @Valid SettlementRequest request
    );

}
