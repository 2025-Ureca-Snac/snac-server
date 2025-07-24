package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.DisputeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisputeResolveRequest {
    private DisputeStatus result;  // RESOLVED or REJECTED
    private String adminComment;
}