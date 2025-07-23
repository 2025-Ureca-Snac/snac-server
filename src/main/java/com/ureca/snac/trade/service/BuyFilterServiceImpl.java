package com.ureca.snac.trade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.common.RedisKeyConstants;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import com.ureca.snac.trade.service.interfaces.BuyFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.ureca.snac.common.RedisKeyConstants.BUYER_FILTER_PREFIX;

@Slf4j
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

    @Override
    public Map<String, BuyerFilterRequest> findAllBuyerFilters() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.BUYER_FILTER_PREFIX + "*");
        Map<String, BuyerFilterRequest> result = new HashMap<>();

        for (String key : keys) {
            String username = key.substring(RedisKeyConstants.BUYER_FILTER_PREFIX.length());
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) continue;
            try {
                BuyerFilterRequest filter = objectMapper.readValue(json, BuyerFilterRequest.class);
                result.put(username, filter);
            } catch (JsonProcessingException e) {
                // 파싱 에러 로그만 남기고 계속
                log.warn("BoughtFilterRequest JSON 파싱 실패 for key={} json={}", key, json, e);
            }
        }
        return result;
    }

//    @Override
//    public void deactivateBuyerFilterByUsername(String username) {
//        String key = RedisKeyConstants.BUYER_FILTER_PREFIX + username;
//        String json = redisTemplate.opsForValue().get(key);
//
//        if (json == null) {
//            log.warn("해당 사용자의 BuyerFilterRequest가 없습니다. username={}", username);
//            return;
//        }
//
//        try {
//            BuyerFilterRequest filter = objectMapper.readValue(json, BuyerFilterRequest.class);
//            filter.setActive(false); // active 필드를 false로 변경
//            String updatedJson = objectMapper.writeValueAsString(filter);
//            redisTemplate.opsForValue().set(key, updatedJson);
//
//        } catch (JsonProcessingException e) {
//            log.warn("BuyerFilterRequest JSON 파싱/저장 실패 for username={} json={}", username, json, e);
//        }
//    }

    @Override
    public void deleteBuyerFilterByUsername(String username) {
        String key = RedisKeyConstants.BUYER_FILTER_PREFIX + username;

        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("구매자 필터 삭제: {}", username);
        }

    }
}
