package com.ureca.snac.common.advice;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("Exception: ", e);

        return ResponseEntity
                .status(e.getBaseCode().getStatus())
                .body(ApiResponse.ok(e.getBaseCode()));
    }
}
