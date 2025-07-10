package com.ureca.snac.auth.exception;

import com.ureca.snac.common.BaseCode;
import lombok.Getter;

@Getter
public class VerificationFailedException extends RuntimeException {
    private final BaseCode baseCode;

    public VerificationFailedException(BaseCode baseCode) {
        super((baseCode.getMessage()));
        this.baseCode = baseCode;
    }
}
