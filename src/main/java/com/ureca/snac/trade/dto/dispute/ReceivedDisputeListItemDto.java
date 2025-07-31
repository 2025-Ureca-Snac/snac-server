package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReceivedDisputeListItemDto {
    private Long disputeId;
    private DisputeStatus status;
    private DisputeType type;
    private LocalDateTime createdAt;
    private TradeSummaryDto trade;
}