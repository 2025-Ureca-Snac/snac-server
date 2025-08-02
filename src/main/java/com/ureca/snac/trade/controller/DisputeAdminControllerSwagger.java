package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "신고/문의 어드민 관리", description = "관리자 전용 신고/문의 답변, 환불, 패널티, 복구 기능")
@SecurityRequirement(name = "Authorization")
@RequestMapping("/api/admin/disputes")
public interface DisputeAdminControllerSwagger {

    @Operation(
        summary = "신고/문의 답변 처리",
        description = "신고/문의에 대해 답변 및 상태를 변경합니다. (자료 추가 요청, 기각, 답변완료)"
    )
    @ApiSuccessResponse(description = "답변 처리 성공")
    @ErrorCode400(description = "잘못된 입력 또는 상태")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @PatchMapping("/{id}/resolve")
    ResponseEntity<ApiResponse<?>> answer(
        @PathVariable Long id,
        @Valid @RequestBody DisputeAnswerRequest disputeAnswerRequest,
        @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
        summary = "신고/문의 전체 검색",
        description = "상태, 유형, 신고자 등 조건별로 신고/문의 목록을 조회합니다. (페이징 지원, 관리자만 접근 가능)"
    )
    @ApiSuccessResponse(description = "목록 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @GetMapping
    ResponseEntity<ApiResponse<Page<DisputeDetailResponse>>> search(
        @RequestParam(required=false) DisputeStatus status,
        @RequestParam(required=false) DisputeType type,
        @RequestParam(required=false) String reporter,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size
    );

    @Operation(
        summary = "처리 대기 신고/문의 목록",
        description = "IN_PROGRESS 상태(진행 중) 신고/문의만 페이징 조회합니다."
    )
    @ApiSuccessResponse(description = "목록 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @GetMapping("/pending")
    ResponseEntity<ApiResponse<Page<DisputeDetailResponse>>> pending(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size
    );

    @Operation(
        summary = "신고/문의 상세 조회",
        description = "특정 신고/문의의 상세 정보를 확인합니다."
    )
    @ApiSuccessResponse(description = "상세 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode404(description = "신고(문의) 내역을 찾을 수 없음")
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<DisputeDetailResponse>> detailDispute(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
        summary = "신고/문의 환불 및 거래취소",
        description = "관리자가 답변 완료(ANSWERED) 상태의 신고/문의 건에 대해 환불 및 거래취소를 처리합니다."
    )
    @ApiSuccessResponse(description = "환불 및 거래취소 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode404(description = "신고(문의) 내역을 찾을 수 없음")
    @PostMapping("/{id}/refund-and-cancel")
    ResponseEntity<ApiResponse<Void>> refundAndCancel(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails admin
    );

    @Operation(
        summary = "판매자 패널티 부여",
        description = "관리자가 신고/문의에 대해 판매자에게 패널티를 부여합니다."
    )
    @ApiSuccessResponse(description = "패널티 부여 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode404(description = "신고(문의) 내역을 찾을 수 없음")
    @PostMapping("/{id}/penalty-seller")
    ResponseEntity<ApiResponse<Void>> penaltySeller(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails admin
    );

    @Operation(
        summary = "신고/문의 복구 시도",
        description = "해당 거래에 활성(IN_PROGRESS/NEED_MORE) 신고가 0개면 거래를 원상복구합니다."
    )
    @ApiSuccessResponse(description = "복구 처리 결과 반환")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode404(description = "신고(문의) 내역을 찾을 수 없음")
    @PostMapping("/{id}/finalize")
    ResponseEntity<ApiResponse<Void>> finalizeIfNoActive(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails admin
    );
}