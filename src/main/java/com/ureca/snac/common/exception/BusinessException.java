package com.ureca.snac.common.exception;

import com.ureca.snac.common.BaseCode;
import lombok.Getter;

@Getter
public class BusinessException extends BaseCustomException {

    public BusinessException(BaseCode baseCode) {
        super(baseCode);
    }
}
