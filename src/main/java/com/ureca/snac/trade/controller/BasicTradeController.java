package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
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
        tradeFacade.sendTradeData(tradeId, userDetails.getUsername(), file);

        return ResponseEntity.ok(ApiResponse.ok(TRADE_DATA_SENT_SUCCESS));
    }

    @PatchMapping("/{tradeId}/confirm")
    public ResponseEntity<ApiResponse<?>> confirmTrade(@PathVariable Long tradeId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.confirmTrade(tradeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_CONFIRM_SUCCESS));
    }

    @GetMapping("/scroll")
    public ResponseEntity<ApiResponse<ScrollTradeResponse>> scrollTrades(@RequestParam TradeSide side,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(required = false, name = "cursorId") Long cursorId,
                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.of(TRADE_SCROLL_SUCCESS,
                tradeFacade.scrollTrades(userDetails.getUsername(), side, size, cursorId)));
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
