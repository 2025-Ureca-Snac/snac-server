package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.trade.service.MatchingServiceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ureca.snac.common.BaseCode.TRADE_DATA_SENT_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingControllerMVC {

    private final MatchingServiceFacade matchingServiceFacade;

    @PatchMapping(value = "/{tradeId}/send-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> sendTradeData(@PathVariable Long tradeId,
                                                        @RequestPart("file") MultipartFile file,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[거래 데이터 전송] PATCH /api/matching/{}/send-data 호출됨", tradeId);
        matchingServiceFacade.sendTradeData(tradeId, file, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_DATA_SENT_SUCCESS));
    }
}
