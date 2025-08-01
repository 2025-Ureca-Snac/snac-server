package com.ureca.snac.trade.controller.request;

import com.ureca.snac.trade.entity.DisputeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateDisputeRequest {
    private Long tradeId;
    private DisputeType type;
    private String title;
    private String description;
    private List<String> attachmentKeys;


}