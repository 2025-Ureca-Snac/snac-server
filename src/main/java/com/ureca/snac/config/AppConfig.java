package com.ureca.snac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}

/*
 * RestTemplate가 구식이라고 하는 이유로 WebClient 추천
 * 그런데 굳이 비동기를 안쓸꺼라면 쓸 이유를 모르겠다
 * RestClient 동기에다가 더 최신 HTTP 클라이언트
 * 의존성도 필요없음
 */