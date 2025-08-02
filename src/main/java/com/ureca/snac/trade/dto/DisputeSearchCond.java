package com.ureca.snac.trade.dto;

import com.ureca.snac.trade.entity.DisputeCategory;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisputeSearchCond {
    private DisputeStatus status;
    private DisputeType type;
    private String reporter;
    private DisputeCategory category;
}