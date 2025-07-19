package com.ureca.snac.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class TradeMessageDto {
    private List<String> phoneList;
    private String message;
}
