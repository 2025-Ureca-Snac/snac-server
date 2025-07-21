package com.ureca.snac.trade.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor // 모든 필드 포함한 생성자
public class AttachmentResponseDto {
    private Long attachmentId; // 첨부 파일 ID
    private String key; // s3 고유키, Presigned Url 아님
}