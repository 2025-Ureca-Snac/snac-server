package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisputeCreateRequest {
    private DisputeType type;
    private String description;
}
