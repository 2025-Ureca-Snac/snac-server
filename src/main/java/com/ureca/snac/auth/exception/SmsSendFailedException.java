package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import lombok.Getter;

@Getter
public class SmsSendFailedException extends RuntimeException {
    private final BaseCode baseCode;

    public SmsSendFailedException(BaseCode baseCode) {
        super(baseCode.getMessage());
        this.baseCode = baseCode;
    }
}