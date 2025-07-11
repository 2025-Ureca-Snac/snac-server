package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.ureca.snac.wallet")
public class WalletExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생{}", e.getMessage());

        return ResponseEntity.status(e.getBaseCode().getStatus())
                .body(ApiResponse.error(e.getBaseCode()));
    }
}
