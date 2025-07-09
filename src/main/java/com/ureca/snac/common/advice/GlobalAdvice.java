package com.ureca.snac.common.advice;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("Exception: ", e);

        return ResponseEntity
                .status(e.getBaseCode().getStatus())
                .body(ApiResponse.error(e.getBaseCode()));
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
