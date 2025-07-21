package com.ureca.snac.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import com.ureca.snac.trade.service.interfaces.BuyFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuyFilterServiceImpl implements BuyFilterService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String PREFIX = "buyer_filter:";

    @Override
    public void saveBuyerFilter(String username, BuyerFilterRequest filter) {
        String key = PREFIX + username;

        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(filter));

        } catch (Exception e) {
            throw new RuntimeException("필터 저장 실패", e);
        }
    }
}
