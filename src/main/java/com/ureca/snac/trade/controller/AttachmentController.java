package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.AttachmentRequestDto;
import com.ureca.snac.trade.dto.AttachmentResponseDto;
import com.ureca.snac.trade.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    // 거래 스크린샷 업로드
    // Content-Type: multipart/form-data
    // image: <file>
    @PostMapping(value = "/{tradeId}/attachment",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AttachmentResponseDto>> upload(
            @PathVariable Long tradeId,
            @RequestPart("image") MultipartFile image,
            // SecurityContext 에서 id 꺼내기 (expression = "id" 는 사용자 정의 UserDetails 구현체 기준)
            @AuthenticationPrincipal UserDetails userDetails) {

        AttachmentRequestDto dto = new AttachmentRequestDto(image);
        String email = userDetails.getUsername();

        AttachmentResponseDto result =
                attachmentService.upload(tradeId, email, dto);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_UPLOAD_SUCCESS, result));
    }

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