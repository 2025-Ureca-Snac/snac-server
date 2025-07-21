package com.ureca.snac.asset.controller;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryListResponse;
import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode500;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "자산 내역 조회", description = "사용자의 자산 변동 내역을 관리")
@RequestMapping("/api/asset-histories")
public interface AssetHistorySwagger {

    @Operation(summary = "내 자산 내역 목록 조회",
            description = "인증된 사용자의 자산 내역(스낵 머니 나 포인트) 조회")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "자산 내역 조회 성공")
    @ErrorCode400(description = "잘못된 요청 파라미터입니다.")
    @ErrorCode401
    @ErrorCode403(description = "권한없음")
    @ErrorCode500
    @GetMapping("/me")
    ResponseEntity<ApiResponse<AssetHistoryListResponse>> getMyAssetHistories(
            @ParameterObject @Valid @ModelAttribute AssetHistoryListRequest request,
            @UserInfo CustomUserDetails userDetails
    );
}
