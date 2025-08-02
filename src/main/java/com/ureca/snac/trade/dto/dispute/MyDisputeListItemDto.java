package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeCategory;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyDisputeListItemDto {
    private Long disputeId;
    private DisputeStatus status;
    private DisputeType type;
    private String title;
    private String description;
    private DisputeCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime answerAt;
    private TradeSummaryDto trade;
}