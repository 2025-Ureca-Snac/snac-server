package com.ureca.snac.board.controller;


import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.board.controller.request.CreateCardRequest;
import com.ureca.snac.board.controller.request.UpdateCardRequest;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.response.ScrollCardResponse;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "판매글/구매글 관리", description = "판매글(SELL), 구매글(BUY) 등록, 조회, 삭제, 수정 기능 제공")
@SecurityRequirement(name = "Authorization")
public interface CardControllerSwagger {

    @Operation(
            summary = "판매글 또는 구매글 등록",
            description = "새로운 판매글(SELL) 또는 구매글(BUY)을 등록합니다. 입력값이 유효해야 합니다."
    )
    @ApiCreatedResponse(description = "등록 성공")
    @ErrorCode400(description = "등록 실패 - 입력값이 잘못되었습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @PostMapping
    ResponseEntity<ApiResponse<?>> createCard(@Validated @RequestBody CreateCardRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(
            summary = "판매글 또는 구매글 수정",
            description = "기존에 등록된 판매글(SELL) 또는 구매글(BUY)의 정보를 수정합니다."
    )
    @ApiSuccessResponse(description = "수정 성공")
    @ErrorCode400(description = "수정 실패 - 입력값이 잘못되었습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "존재하지 않는 글 ID")
    @PutMapping("/{cardId}")
    ResponseEntity<ApiResponse<?>> editCard(@PathVariable("cardId") Long cardId, @Validated @RequestBody UpdateCardRequest updateCardRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(
            summary = "판매글 또는 구매글 목록 조회 (스크롤)",
            description = """
        조건에 맞는 판매글(SELL) 또는 구매글(BUY)을 스크롤 방식으로 조회합니다.
        - `cardCategory`, `priceRanges`는 필수이며, SELL 또는 BUY 중 하나  
        - `carrier`는 선택적 필터  
        - 커서 페이징 방식 사용  
        - 초기 조회 시에는 `lastCardId`, `lastUpdatedAt` 없이 호출 가능  
        - 이후 추가 조회(더보기) 시에는 두 값 모두 전달해야 합니다.
        """
    )
    @ApiSuccessResponse(description = "목록 조회 성공")
    @ErrorCode400(description = "조회 실패 - 잘못된 요청 파라미터")
    @ErrorCode404(description = "조회 실패 - 존재하지 않는 판매글 또는 구매글")
    @GetMapping("/scroll")
    ResponseEntity<ApiResponse<ScrollCardResponse>> scrollCards(@RequestParam CardCategory cardCategory,
                                                                       @RequestParam(required = false) Carrier carrier,
                                                                       @RequestParam(value = "priceRanges") List<PriceRange> priceRanges,
                                                                       @RequestParam(defaultValue = "54") int size,
                                                                       @RequestParam(required = false) Long lastCardId,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdatedAt);

    @Operation(
            summary = "판매글 또는 구매글 삭제",
            description = "등록된 판매글(SELL) 또는 구매글(BUY)을 삭제합니다."
    )
    @ApiSuccessResponse(description = "삭제 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "삭제 실패 - 존재하지 않는 글 ID")
    @DeleteMapping("/{cardId}")
    ResponseEntity<ApiResponse<?>> removeCard(@PathVariable("cardId") Long cardId, @AuthenticationPrincipal CustomUserDetails customUserDetails);
}
