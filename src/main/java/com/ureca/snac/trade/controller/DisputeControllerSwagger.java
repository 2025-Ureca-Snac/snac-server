package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode403;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import com.ureca.snac.trade.dto.dispute.DisputeCreateRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.dto.dispute.MyDisputeListItemDto;
import com.ureca.snac.trade.dto.dispute.QnaCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "신고/문의 관리", description = "거래 신고 및 QnA(문의) 관련 기능")
@SecurityRequirement(name = "Authorization")
@RequestMapping("/api")
public interface DisputeControllerSwagger {

    @Operation(
            summary = "거래 신고(문의) 등록",
            description = "특정 거래에 대한 신고를 등록합니다. 첨부파일은 미리 presigned url로 S3에 업로드 후 키만 전달합니다."
    )
    @ApiCreatedResponse(description = "신고 등록 성공")
    @ErrorCode400(description = "입력값이 올바르지 않음")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode403(description = "해당 거래에 대한 권한 없음")
    @PostMapping("/trades/{tradeId}/disputes")
    ResponseEntity<ApiResponse<?>> createDispute(
            @PathVariable Long tradeId,
            @Valid @RequestBody DisputeCreateRequest disputeCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "신고(문의) 상세 조회",
            description = "내가 신고한 거래 또는 내 QnA(문의) 상세 내용을 확인합니다. 첨부이미지는 presigned url로 반환됩니다."
    )
    @ApiSuccessResponse(description = "상세 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @ErrorCode403(description = "접근 권한 없음")
    @ErrorCode404(description = "신고(문의) 내역을 찾을 수 없음")
    @GetMapping("/disputes/{id}")
    ResponseEntity<ApiResponse<?>> detailDispute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "내 신고/문의 내역 조회",
            description = "내가 등록한 신고 및 QnA(문의) 목록을 조회합니다. 페이징 지원."
    )
    @ApiSuccessResponse(description = "목록 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @GetMapping("/disputes/mine")
    ResponseEntity<ApiResponse<?>> myDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "QnA(일반 문의) 등록",
            description = "거래와 무관한 일반 문의(QnA)를 등록합니다. 첨부파일은 미리 presigned url로 S3에 업로드 후 키만 전달합니다."
    )
    @ApiCreatedResponse(description = "QnA 등록 성공")
    @ErrorCode400(description = "입력값이 올바르지 않음")
    @ErrorCode401(description = "인증되지 않은 사용자")
    @PostMapping("/qna")
    ResponseEntity<ApiResponse<?>> createQna(
            @Valid @RequestBody QnaCreateRequest qnaCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    );
}