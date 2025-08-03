package com.ureca.snac.trade.controller;

import com.ureca.snac.auth.dto.response.ImageValidationResult;
import com.ureca.snac.auth.service.lmm.TradeImageValidationService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
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
    private final TradeImageValidationService tradeImageValidationService;

    @PatchMapping(value = "/{tradeId}/send-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> sendTradeData(@PathVariable Long tradeId,
                                                        @RequestPart("file") MultipartFile file,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("[거래 데이터 전송] PATCH /api/matching/{}/send-data 호출됨", tradeId);

        // lmm 도입

        // 추후 3자(skt, lg, 또 한개 뭐였지)와 합의 후, 추출된 사진에서의 데이터를 직접 보내어 실제로 데이터 전달이 이루어졌는지 확인하는
        // 확장성을 고려한 기술 도입
        // 현재의 목적은 데이터 전송 사진 외에 사적 사진 제재
        ImageValidationResult validation = tradeImageValidationService.validateImage(file);
        if (!validation.valid()) {
            String message = validation.message();
            return ResponseEntity.ok(ApiResponse.of(BaseCode.IMAGE_CRITERIA_REJECTED, message));
        }

        matchingServiceFacade.sendTradeData(tradeId, file, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(TRADE_DATA_SENT_SUCCESS));
    }
}
