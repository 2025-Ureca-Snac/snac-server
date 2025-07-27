package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.trade.service.interfaces.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final S3Uploader s3Uploader;

    // 거래 이미지 Presigned URL 발급
    @GetMapping("/{tradeId}/attachment-url")
    public ResponseEntity<ApiResponse<String>> getPresignedUrl(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        String url = attachmentService.generatePresignedUrl(tradeId, email);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_PRESIGNED_URL_ISSUED, url));
    }

    // 신고할때 이미지 올리기
    @PostMapping(value = "/dispute/attachment",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> upload(
            @RequestPart("files") List<MultipartFile> files) {

        List<String> keys = files.stream()
                .map(file -> s3Uploader.upload(file, "disputes/attachments"))
                .toList();

        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_UPLOAD_SUCCESS, keys));
    }
}
