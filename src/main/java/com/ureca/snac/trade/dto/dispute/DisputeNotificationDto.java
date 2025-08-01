package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeNotificationDto {
    private Long disputeId;
    private Long tradeId;
    private DisputeType type;
    private String reporter;
}