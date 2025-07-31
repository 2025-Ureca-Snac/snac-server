package com.ureca.snac.trade.dto.dispute;

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
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime answerAt;
    private TradeSummaryDto trade;
}