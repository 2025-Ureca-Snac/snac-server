package com.ureca.snac.trade.controller;

import com.ureca.snac.auth.dto.response.ImageValidationResult;
import com.ureca.snac.auth.service.lmm.TradeImageValidationService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.controller.request.TradeQueryType;
import com.ureca.snac.trade.dto.CancelTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.TradeFacade;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeIdResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ureca.snac.common.BaseCode.*;

@Slf4j
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class BasicTradeController implements BasicTradeControllerSwagger {

    private final TradeFacade tradeFacade;
    private final TradeImageValidationService tradeImageValidationService;


    @GetMapping("/count/buy")
    public ResponseEntity<ApiResponse<ProgressTradeCountResponse>> countProgressBuyTrades(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.of(TRADE_PROGRESS_COUNT_SUCCESS,
                tradeFacade.countBuyingProgress(userDetails.getUsername())));
    }

    @GetMapping("/count/sell")
    public ResponseEntity<ApiResponse<ProgressTradeCountResponse>> countProgressSellTrades(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.of(TRADE_PROGRESS_COUNT_SUCCESS,
                tradeFacade.countSellingProgress(userDetails.getUsername())));
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<?>> createSellTrade(@RequestBody CreateTradeRequest createTradeRequest,
                                                          @AuthenticationPrincipal UserDetails userDetails) {

        Long sellTradeId = tradeFacade.createSellTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(TRADE_CREATE_SUCCESS, new TradeIdResponse(sellTradeId)));
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<?>> createBuyTrade(@RequestBody CreateTradeRequest createTradeRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long buyTradeId = tradeFacade.createBuyTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(TRADE_CREATE_SUCCESS, new TradeIdResponse(buyTradeId)));
    }

    @PostMapping("/buy/accept")
    public ResponseEntity<ApiResponse<?>> acceptBuyRequest(@RequestBody ClaimBuyRequest claimBuyRequest,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        Long acceptedTradeId = tradeFacade.acceptBuyRequest(claimBuyRequest, userDetails.getUsername());
        return ResponseEntity
                .ok(ApiResponse.of(TRADE_ACCEPT_SUCCESS, new TradeIdResponse(acceptedTradeId)));
    }

    @PatchMapping(value = "/{tradeId}/send-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> sendTradeData(@PathVariable Long tradeId,
                                                        @RequestPart("file") MultipartFile file,
                                                        @AuthenticationPrincipal UserDetails userDetails) {

        // lmm 도입

        // 추후 3자(skt, lg, 또 한개 뭐였지)와 합의 후, 추출된 사진에서의 데이터를 직접 보내어 실제로 데이터 전달이 이루어졌는지 확인하는
        // 확장성을 고려한 기술 도입
        // 현재의 목적은 데이터 전송 사진 외에 사적 사진 제재
        ImageValidationResult validation = tradeImageValidationService.validateImage(file);
        if (!validation.valid()) {
            String message = validation.message();
            return ResponseEntity.ok(ApiResponse.of(BaseCode.IMAGE_CRITERIA_REJECTED, message));
        }

        tradeFacade.sendTradeData(tradeId, userDetails.getUsername(), file);

        return ResponseEntity.ok(ApiResponse.ok(TRADE_DATA_SENT_SUCCESS));
    }

    @PatchMapping("/{tradeId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmTrade(@PathVariable Long tradeId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.confirmTrade(tradeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_CONFIRM_SUCCESS));
    }

    @GetMapping("/scroll")
    public ResponseEntity<ApiResponse<ScrollTradeResponse>> scrollTrades(@RequestParam TradeSide side,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(required = false) TradeQueryType tradeQueryType,
                                                                         @RequestParam(required = false, name = "cursorId") Long cursorId,
                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.of(TRADE_SCROLL_SUCCESS,
                tradeFacade.scrollTrades(userDetails.getUsername(), side, size, tradeQueryType, cursorId)));
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<TradeResponse>> retrieveTrade(@PathVariable("tradeId") Long tradeId,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.of(TRADE_READ_SUCCESS,
                tradeFacade.getTradeById(tradeId, userDetails.getUsername())));
    }

    @PatchMapping("/{tradeId}/cancel/request")
    public ResponseEntity<ApiResponse<?>> requestCancel(@PathVariable Long tradeId,
                                                        @RequestBody @Valid CancelTradeRequest dto,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.requestCancel(tradeId, userDetails.getUsername(), dto.getReason());
        return ResponseEntity.ok(ApiResponse.ok(TRADE_CANCEL_REQUESTED));
    }

    @PatchMapping("/{tradeId}/cancel/accept")
    public ResponseEntity<ApiResponse<?>> acceptCancel(@PathVariable Long tradeId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.acceptCancel(tradeId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(TRADE_CANCEL_ACCEPTED));
    }

    @PatchMapping("/{tradeId}/cancel/reject")
    public ResponseEntity<ApiResponse<?>> rejectCancel(@PathVariable Long tradeId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.rejectCancel(tradeId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(TRADE_CANCEL_REJECTED));
    }
}
