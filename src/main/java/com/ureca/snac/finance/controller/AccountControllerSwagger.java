package com.ureca.snac.finance.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.finance.controller.request.CreateAccountRequest;
import com.ureca.snac.finance.controller.request.UpdateAccountRequest;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "계좌 관리", description = "사용자 계좌 생성·조회·수정·삭제 기능 제공")
@SecurityRequirement(name = "Authorization")
public interface AccountControllerSwagger {

    @Operation(
            summary = "계좌 등록",
            description = "로그인한 사용자의 새 계좌를 생성합니다. 요청 데이터 유효성 검증이 필요합니다."
    )
    @ApiCreatedResponse(description = "계좌 생성 성공")
    @ErrorCode400(description = "계좌 생성 실패 - 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @PostMapping
    ResponseEntity<ApiResponse<?>> createAccount(@Validated @RequestBody CreateAccountRequest createAccountRequest, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "계좌 목록 조회",
            description = "로그인한 사용자의 모든 계좌 정보를 조회합니다."
    )
    @ApiSuccessResponse(description = "계좌 목록 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    ResponseEntity<ApiResponse<?>> getAccounts(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "계좌 정보 수정",
            description = "특정 계좌의 정보를 수정합니다. 계좌가 존재하지 않으면 404 에러를 반환합니다."
    )
    @ApiSuccessResponse(description = "계좌 수정 성공")
    @ErrorCode400(description = "계좌 수정 실패 - 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "계좌 수정 실패 - 해당 계좌를 찾을 수 없습니다.")
    ResponseEntity<ApiResponse<?>> editAccount(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable("accountId") Long accountId,
                                               @Validated @RequestBody UpdateAccountRequest updateAccountRequest);

    @Operation(
            summary = "계좌 삭제",
            description = "특정 계좌를 삭제합니다. 계좌가 존재하지 않으면 404 에러를 반환합니다."
    )
    @ApiSuccessResponse(description = "계좌 삭제 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "계좌 삭제 실패 - 해당 계좌를 찾을 수 없습니다.")
    ResponseEntity<ApiResponse<?>> removeAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("accountId") Long accountId);
}
