package com.ureca.snac.common.exception;

import com.ureca.snac.common.BaseCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 서버 내부에서 발생하는 비즈니스 예외가 아닌 복구 불가능한 예외
 * 500 상태코드 반환
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends BaseCustomException {

    public InternalServerException(BaseCode baseCode) {
        super(baseCode);
    }
}
