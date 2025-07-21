package com.ureca.snac.common.advice;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BaseCustomException;
import com.ureca.snac.common.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BaseCustomException e) {
        BaseCode baseCode = e.getBaseCode();

        if (e instanceof ExternalApiException) {
            log.error("외부 API 호출 예외 발생 : {}", e.getMessage(), e);
        } else {
            log.warn("내부 비즈니스 예외 발생 : ", e);
        }

        return ResponseEntity
                .status(baseCode.getStatus())
                .body(ApiResponse.error(baseCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation fail: ", e);

        List<ValidationErrorResponse> fieldErrors = e.getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getCode(),
                        error.getDefaultMessage()
                ))
                .toList();

        List<ValidationErrorResponse> globalErrors = e.getGlobalErrors()
                .stream()
                .map(error -> new ValidationErrorResponse(
                        error.getObjectName(),
                        "",
                        error.getCode(),
                        error.getDefaultMessage()
                )).toList();

        return ResponseEntity.status(BaseCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.of(BaseCode.INVALID_INPUT, ValidationErrorResponseWrapper.of(fieldErrors, globalErrors)));
    }
}
