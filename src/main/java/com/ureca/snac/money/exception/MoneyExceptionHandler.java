package com.ureca.snac.money.exception;

import com.ureca.snac.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(basePackages = "com.ureca.snac.money")
@RequiredArgsConstructor
public class MoneyExceptionHandler {

    @Value("${payments.toss.fail-url}")
    private String failUrl;

    @ExceptionHandler(BusinessException.class)
    public void handlePaymentRedirectException(BusinessException e, HttpServletResponse response) throws IOException {
        String errorCode = e.getBaseCode().getCode();
        String message = e.getMessage();

        log.warn("결제 처리 중 비즈니스 예외 발생: code {}, message={}", errorCode, message);

        response.sendRedirect(failUrl + "?code=" + errorCode + "&message=" + message);
    }
}
