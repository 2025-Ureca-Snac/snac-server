package com.ureca.snac.favorite.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteCheckResponse;
import com.ureca.snac.favorite.dto.FavoriteCreateRequest;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.error.ErrorCode409;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "단골 관리",
        description = "단골 등록, 조회, 삭제와 관련된 API")
@RequestMapping("/api/favorites")
public interface FavoriteSwagger {
    @Operation(summary = "단골 등록",
            description = "특정 사용자를 내 단골 목록에 추가합니다")
    @SecurityRequirement(name = "Authorization")
    @ApiCreatedResponse(description = "단골 등록 성공")
    @ErrorCode400(description = "자기 자신을 등록할 수 없습니다.")
    @ErrorCode401
    @ErrorCode404(description = "단골로 등록할 사용자가 없습니다")
    @ErrorCode409(description = "이미 단골로 등록된 사용자입니다")
    @PostMapping
    ResponseEntity<ApiResponse<Void>> createFavorite(
            @Valid @RequestBody FavoriteCreateRequest request,
            @UserInfo CustomUserDetails userDetails
    );

    @Operation(summary = "내 단골 목록 최신순 조회",
            description = "커서 기반 페이지네이션으로 내단골 목록 최신 등록순 조회")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "단골 목록 조회 성공")
    @ErrorCode401
    @ErrorCode404(description = "사용자를 찾을 수 없습니다")
    @GetMapping
    ResponseEntity<ApiResponse<CursorResult<FavoriteMemberDto>>> getMyFavorites(
            @Parameter(description = "이전 페이지의 마지막 항목의 생성 시간")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,

            @Parameter(description = "이전 페이지의 마지막 항목의 ID")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "페이지에 보여줄 항목 수")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @UserInfo CustomUserDetails userDetails
    );

    @Operation(summary = "단골 삭제",
            description = "내 단골 목록에서 특정 사용자 삭제합니다")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "단골 삭제 성공")
    @ErrorCode401
    @ErrorCode404(description = "삭제할 단골을 찾을 수 없습니다")
    @DeleteMapping("/{toMemberId}")
    ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long toMemberId,
            @UserInfo CustomUserDetails userDetails
    );

    @Operation(summary = "단골 여부",
            description = "특정 사용자가 나의 단골인지 여부 확인")
    @GetMapping("/check")
    ResponseEntity<ApiResponse<FavoriteCheckResponse>> checkFavoriteStatus(
            @Parameter(description = "확인할 대상의 회원 ID", required = true)
            @RequestParam Long toMemberId,
            @UserInfo CustomUserDetails userDetails
    );
}
