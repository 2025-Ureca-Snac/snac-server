package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeCategory;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DisputeDetailResponse {
    private Long id;
    private DisputeStatus status;
    private DisputeType type;
    private String title;
    private String description;
    private String answer;
    private DisputeCategory category;

    private List<String> attachmentUrls;
    private LocalDateTime createdAt;
    private LocalDateTime answerAt;
}