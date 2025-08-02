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
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final S3Uploader s3Uploader;

    // 거래 이미지 Presigned URL 발급
    @GetMapping("/api/trades/{tradeId}/attachment-url")
    public ResponseEntity<ApiResponse<String>> getPresignedUrl(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        String url = attachmentService.generatePresignedUrl(tradeId, email);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_PRESIGNED_URL_ISSUED, url));
    }

    // 신고할때 이미지 올리기 (백엔드 거쳐서 업로드)
    // 안쓸거
    @PostMapping(value = "/api/trades/dispute/attachment",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> upload(
            @RequestPart("files") List<MultipartFile> files) {

        List<String> keys = files.stream()
                .map(file -> s3Uploader.upload(file, "disputes/attachments"))
                .toList();

        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_UPLOAD_SUCCESS, keys));
    }

    // 프론트에서 바로 업로드하는 뉴 방법
    // 서버는 presigned url 을 만들어주기만 하고 실제 업로드는 s3가
    // s3에 직접 put 방식으로
    @PostMapping("/api/attachments/upload-url") // 업로드용 url 얻는 api
    public ResponseEntity<ApiResponse<Map<String, String>>> getDisputePresignedUrl(
            @RequestParam("filename") String filename) {
        String s3Key = s3Uploader.buildKey(filename, "disputes/attachments");
        String url = s3Uploader.generatePresignedPutUrl(s3Key); // PUT presigned url
        Map<String, String> data = Map.of(
                "s3Key", s3Key,
                "uploadUrl", url
        );

        return ResponseEntity.ok(ApiResponse.of(BaseCode.ATTACHMENT_PRESIGNED_UPLOAD_URL_ISSUED, data));
    }
}
