package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DisputeCreateRequest {
    private DisputeType type;
    private String description;
    private List<String> attachmentKeys; // s3 키 목록
}
