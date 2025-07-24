package com.ureca.snac.infra.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml에 정의된 토스페이먼츠 관련 설정 객체 바인딩
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "payments.toss")
public class TossPaymentProperties {

    private final String url;

    private final String secretKey;
}