package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.*;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.controller.request.TradeQueryType;
import com.ureca.snac.trade.dto.TradeConfirmResponse;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "거래 관리", description = "거래 생성·조회·취소·확정·데이터 전송 기능")
@SecurityRequirement(name = "Authorization")
public interface BasicTradeControllerSwagger {

    @Operation(summary = "진행 중인 구매 거래 수 조회", description = "로그인한 사용자의 진행 중인 구매 거래 건수를 조회합니다.")
    @ApiSuccessResponse(description = "진행 중인 구매 거래 수 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @GetMapping("/count/buy")
    ResponseEntity<ApiResponse<ProgressTradeCountResponse>> countProgressBuyTrades(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "진행 중인 판매 거래 수 조회", description = "로그인한 사용자의 진행 중인 판매 거래 건수를 조회합니다.")
    @ApiSuccessResponse(description = "진행 중인 판매 거래 수 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @GetMapping("/count/sell")
    ResponseEntity<ApiResponse<ProgressTradeCountResponse>> countProgressSellTrades(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "판매 거래 생성", description = "로그인한 사용자가 카드에 대해 판매 거래 요청을 생성합니다.")
    @ApiCreatedResponse(description = "판매 거래 생성 성공")
    @ErrorCode400(description = "잘못된 요청 – 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "자신의 글에는 거래를 요청할 수 없습니다.")
    @PostMapping("/sell")
    ResponseEntity<ApiResponse<?>> createSellTrade(@Validated @RequestBody CreateTradeRequest createTradeRequest,
                                                   @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "구매 거래 생성", description = "로그인한 사용자가 카드에 대해 구매 거래 요청을 생성합니다.")
    @ApiCreatedResponse(description = "구매 거래 생성 성공")
    @ErrorCode400(description = "잘못된 요청 – 입력값이 올바르지 않습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "타인의 글만 구매 요청이 가능합니다.")
    @PostMapping("/buy")
    ResponseEntity<ApiResponse<?>> createBuyTrade(@Validated @RequestBody CreateTradeRequest createTradeRequest,
                                                  @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "거래 데이터 전송", description = "판매자가 파일을 업로드하여 거래 데이터를 전송합니다.")
    @ApiSuccessResponse(description = "거래 데이터 전송 성공")
    @ErrorCode400(description = "잘못된 거래 상태로 인해 전송할 수 없습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "판매자만 거래 데이터를 전송할 수 있습니다.")
    @ErrorCode404(description = "거래를 찾을 수 없습니다.")
    @PatchMapping(value = "/{tradeId}/send-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<?>> sendTradeData(@PathVariable Long tradeId,
                                                 @RequestPart("file") MultipartFile file,
                                                 @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "거래 확정", description = "구매자가 거래를 확정하고 결제를 완료합니다.")
    @ApiSuccessResponse(description = "거래 확정 성공")
    @ErrorCode400(description = "잘못된 거래 상태로 인해 확정할 수 없습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode403(description = "구매자만 거래를 확정할 수 있습니다.")
    @ErrorCode404(description = "거래를 찾을 수 없습니다.")
    @PatchMapping("/{tradeId}/confirm")
    ResponseEntity<ApiResponse<TradeConfirmResponse>> confirmTrade(@PathVariable Long tradeId,
                                                                   @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "거래 내역 조회 (무한 스크롤)", description = "로그인한 사용자의 거래 내역을 BUY/SELL 관점으로 무한 스크롤 방식으로 조회합니다.")
    @ApiSuccessResponse(description = "거래 내역 조회 성공")
    @ErrorCode400(description = "잘못된 요청 파라미터")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @GetMapping("/scroll")
    ResponseEntity<ApiResponse<ScrollTradeResponse>> scrollTrades(@RequestParam TradeSide side,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(required = false) TradeQueryType tradeQueryType,
                                                                  @RequestParam(required = false, name = "cursorId") Long cursorId,
                                                                  @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "구매글 거래 수락", description = "판매자가 구매글에 대해 거래를 수락하고, 카드 상태를 TRADING으로 변경합니다.")
    @ApiSuccessResponse(description = "거래 수락 성공")
    @ErrorCode400(description = "잘못된 거래 상태로 인해 수락할 수 없습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "거래를 찾을 수 없습니다.")
    @PostMapping("/buy/accept")
    ResponseEntity<ApiResponse<?>> acceptBuyRequest(@RequestBody ClaimBuyRequest claimBuyRequest,
                                                    @AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "단건 거래 조회",
            description = "tradeId를 기반으로 단일 거래 정보를 조회합니다."
    )
    @ApiSuccessResponse(description = "거래 조회 성공")
    @ErrorCode400(description = "잘못된 요청 파라미터")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "거래를 찾을 수 없습니다")
    @GetMapping("/{tradeId}")
    ResponseEntity<ApiResponse<TradeResponse>> retrieveTrade(@PathVariable("tradeId") Long tradeId,
                                                             @AuthenticationPrincipal UserDetails userDetails);
}
