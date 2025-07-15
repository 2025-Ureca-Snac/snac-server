package com.ureca.snac.payment.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class PaymentRedirectExceptionResolver implements HandlerExceptionResolver {

    private final String failUrl;

    public PaymentRedirectExceptionResolver(@Value("${payments.toss.fail-url}") String failUrl) {
        this.failUrl = failUrl;
    }

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        // PaymentRedirectException만 사용

        if (ex instanceof PaymentRedirectException e) {
            try {
                String errorCode = e.getBaseCode().getCode();
                String message = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                String redirectUrl = failUrl + "?code=" + errorCode + "&message=" + message;

                log.warn("결제 실패로 인한 리다이렉트 실행.{}", redirectUrl);
                // 리다이렉트
                response.sendRedirect(redirectUrl);

                return new ModelAndView();
            } catch (IOException ioException) {
                log.error("리다이렉트 중 IO 예외 발생", ioException);
            }
        }
        return null;
    }
}
