package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.controller.request.TradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.interfaces.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/{side}")
    public ResponseEntity<ApiResponse<Long>> requestTrade(@PathVariable("side") TradeSide side,
                                                          @RequestBody TradeRequest tradeRequest,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long tradeId = tradeService.requestTrade(tradeRequest, email, side);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.TRADE_REQUEST_SUCCESS, tradeId));
    }

    // 수락
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<?>> accept(@RequestBody AcceptTradeRequest acceptTradeRequest,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        tradeService.acceptTrade(acceptTradeRequest, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(BaseCode.TRADE_ACCEPT_SUCCESS, null));
    }
}
