package com.ureca.snac.trade.dto;

import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@AllArgsConstructor
public class RetrieveFilterDto {
    private String username;
    private Map<String, BuyerFilterRequest> buyerFilter;
}
