package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.controller.request.AcceptTradeRequest;
import com.ureca.snac.trade.controller.request.TradeRequest;
import com.ureca.snac.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // 구매자 관점에서 판매글에 거래 요청
    @PostMapping("/buyer")
    public ResponseEntity<ApiResponse<?>> requestTradeAsBuyer(@RequestBody TradeRequest tradeRequest,
                                                              @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Long tradeId = tradeService.requestTradeAsBuyer(tradeRequest, email);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, tradeId));
    }

    @PostMapping("/seller")
    public ResponseEntity<ApiResponse<?>> requestTradeAsSeller(@RequestBody TradeRequest tradeRequest,
                                                               @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Long tradeId = tradeService.requestTradeAsSeller(tradeRequest, email);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, tradeId));
    }

    // 수락
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<?>> accept(@RequestBody AcceptTradeRequest acceptTradeRequest,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        tradeService.acceptTrade(acceptTradeRequest, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, null));
    }
}
