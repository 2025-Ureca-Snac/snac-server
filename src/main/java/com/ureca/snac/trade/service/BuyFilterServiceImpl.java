package com.ureca.snac.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import com.ureca.snac.trade.service.interfaces.BuyFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.ureca.snac.common.RedisKeyConstants.BUYER_FILTER_PREFIX;

@Service
@RequiredArgsConstructor
public class BuyFilterServiceImpl implements BuyFilterService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveBuyerFilter(String username, BuyerFilterRequest filter) {
        String key = BUYER_FILTER_PREFIX + username;

        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(filter));

        } catch (Exception e) {
            throw new RuntimeException("필터 저장 실패", e);
        }
    }
}
