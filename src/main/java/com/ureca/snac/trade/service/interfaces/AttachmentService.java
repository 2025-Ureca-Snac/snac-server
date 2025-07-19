package com.ureca.snac.trade.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    void upload(Long tradeId, String userEmail, MultipartFile image); // 첨부 파일을 업로드
    String generatePresignedUrl(Long tradeId, String userEmail); // 특정 첨부 파일에 대한 Presigned URL 을 생성하여 반환
}
