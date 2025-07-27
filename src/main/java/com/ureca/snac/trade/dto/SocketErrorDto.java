package com.ureca.snac.trade.dto;

import com.ureca.snac.common.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SocketErrorDto {
    private BaseCode baseCode;
    private String username;
}
