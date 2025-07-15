package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.service.BasicTradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class BasicTradeController implements BasicTradeControllerSwagger {

    private final BasicTradeService basicTradeService;

    @PostMapping("/sell")
    public ResponseEntity<?> createSellTrade(@RequestBody CreateTradeRequest createTradeRequest,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        basicTradeService.createSellTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(BaseCode.TRADE_CREATE_SUCCESS));
    }

    @PostMapping("/buy")
    public ResponseEntity<?> createBuyTrade(@RequestBody CreateTradeRequest createTradeRequest,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        basicTradeService.createBuyTrade(createTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(BaseCode.TRADE_CREATE_SUCCESS));
    }

    @PatchMapping("/{tradeId}/cancel")
    public ResponseEntity<?> cancelTrade(@PathVariable Long tradeId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        basicTradeService.cancelTrade(tradeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(BaseCode.TRADE_CANCEL_SUCCESS));
    }
}
