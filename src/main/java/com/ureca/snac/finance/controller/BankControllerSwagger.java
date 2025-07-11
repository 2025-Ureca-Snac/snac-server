package com.ureca.snac.finance.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.finance.controller.request.CreateBankRequest;
import com.ureca.snac.finance.controller.request.UpdateBankRequest;
import com.ureca.snac.finance.service.response.BankResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "은행 관리", description = "은행 생성·조회·수정·삭제 기능 (관리자 전용)")
@SecurityRequirement(name = "Authorization")
public interface BankControllerSwagger {

    @Operation(
            summary = "단건 은행 조회",
            description = "ID에 해당하는 은행 정보를 조회합니다."
    )
    @ApiSuccessResponse(description = "은행 조회 성공")
    @ErrorCode404(description = "조회 실패 - 해당 은행을 찾을 수 없습니다.")
    @GetMapping("/{bankId}")
    ResponseEntity<ApiResponse<BankResponse>> getBank(@PathVariable("bankId") Long bankId);

    @Operation(
            summary = "전체 은행 목록 조회",
            description = "등록된 모든 은행 목록을 조회합니다."
    )
    @ApiSuccessResponse(description = "은행 목록 조회 성공")
    @GetMapping
    ResponseEntity<ApiResponse<List<BankResponse>>> getAllBanks();

    @Operation(
            summary = "은행 등록",
            description = "새로운 은행을 생성합니다."
    )
    @ApiCreatedResponse(description = "은행 생성 성공")
    @ErrorCode400(description = "생성 실패 - 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "관리자 권한이 필요합니다.")
    @PostMapping
    ResponseEntity<ApiResponse<?>> createBank(@Validated @RequestBody CreateBankRequest createBankRequest);

    @Operation(
            summary = "은행 삭제",
            description = "ID에 해당하는 은행을 삭제합니다."
    )
    @ApiSuccessResponse(description = "은행 삭제 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "관리자 권한이 필요합니다.")
    @ErrorCode404(description = "삭제 실패 - 해당 은행을 찾을 수 없습니다.")
    @DeleteMapping("/{bankId}")
    ResponseEntity<ApiResponse<?>> deleteBank(@PathVariable("bankId") Long bankId);

    @Operation(
            summary = "은행 정보 수정",
            description = "ID에 해당하는 은행 이름을 변경합니다."
    )
    @ApiSuccessResponse(description = "은행 수정 성공")
    @ErrorCode400(description = "수정 실패 - 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "관리자 권한이 필요합니다.")
    @ErrorCode404(description = "수정 실패 - 해당 은행을 찾을 수 없습니다.")
    @PatchMapping("/{bankId}")
    ResponseEntity<ApiResponse<?>> updateBank(@PathVariable("bankId") Long bankId,
                                              @Validated @RequestBody UpdateBankRequest updateBankRequest);
}
