package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QnaCreateRequest {
    private String title;
    private DisputeType type; // 결제, 계정, 기술 문제 등
    private String description;
    private List<String> attachmentKeys; // 첨부파일 (선택)
}