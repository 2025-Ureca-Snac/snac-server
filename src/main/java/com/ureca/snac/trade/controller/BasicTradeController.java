package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.TradeFacade;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
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

        tradeFacade.createSellTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_CREATE_SUCCESS));
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<?>> createBuyTrade(@RequestBody CreateTradeRequest createTradeRequest,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.createBuyTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_CREATE_SUCCESS));
    }

    @PostMapping("/buy/accept")
    public ResponseEntity<ApiResponse<?>> acceptBuyRequest(@RequestBody ClaimBuyRequest claimBuyRequest,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.acceptBuyRequest(claimBuyRequest, userDetails.getUsername());
        return ResponseEntity
                .ok(ApiResponse.ok(TRADE_ACCEPT_SUCCESS));
    }

    @PatchMapping("/{tradeId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelTrade(@PathVariable Long tradeId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        tradeFacade.cancelTrade(tradeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_CANCEL_SUCCESS));
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
}
