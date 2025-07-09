package com.ureca.snac.auth.exception;

import com.ureca.snac.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ureca.snac.auth")
public class AuthExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity.ok(ApiResponse.of(e.getBaseCode(),null));
    }
}
