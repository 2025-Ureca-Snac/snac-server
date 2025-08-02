package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeCategory;
import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class DisputeStatisticsResponse {
    private Map<DisputeCategory, Long> countByCategory;
    private Map<DisputeType, Long> countByType;
}