package com.ureca.snac.trade.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.AcceptTradeRequest;
import com.ureca.snac.trade.dto.TradeRequest;
import com.ureca.snac.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // 거래 신청
    @PostMapping
    public ResponseEntity<ApiResponse<?>> requestTrade(@RequestBody TradeRequest dto,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String email = customUserDetails.getUsername();

        return ResponseEntity.ok(
                ApiResponse.of(BaseCode.STATUS_OK,
                        tradeService.requestTradeByEmail(dto, email)));
    }

    // 수락
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<?>> accept(@RequestBody AcceptTradeRequest dto,
                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        tradeService.acceptTradeByEmail(dto.getTradeId(),customUserDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of(BaseCode.STATUS_OK, null));
    }
}