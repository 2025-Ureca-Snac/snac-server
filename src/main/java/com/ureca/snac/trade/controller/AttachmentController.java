package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.service.interfaces.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    // 거래 이미지 Presigned URL 발급
    @GetMapping("/{tradeId}/attachment-url")
    public ResponseEntity<ApiResponse<String>> getPresignedUrl(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        String url = attachmentService.generatePresignedUrl(tradeId, email);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_PRESIGNED_URL_ISSUED, url));
    }
}
