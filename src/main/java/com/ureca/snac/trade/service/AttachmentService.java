package com.ureca.snac.trade.service;

import com.ureca.snac.trade.dto.AttachmentRequestDto;
import com.ureca.snac.trade.dto.AttachmentResponseDto;

public interface AttachmentService {

    AttachmentResponseDto upload(Long tradeId, String userEmail, AttachmentRequestDto dto); // 첨부 파일을 업로드
    String generatePresignedUrl(Long tradeId, String userEmail); // 특정 첨부 파일에 대한 Presigned URL 을 생성하여 반환
}