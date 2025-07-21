package com.ureca.snac.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import com.ureca.snac.trade.service.interfaces.BuyFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.ureca.snac.common.RedisKeyConstants.BUYER_FILTER_PREFIX;
import static com.ureca.snac.common.RedisKeyConstants.CONNECTED_USERS;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingServiceFacade {

    private final CardService cardService;
    private final NotificationService notificationService;
    private final BuyFilterService buyFilterService;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public long getConnectedUserCount() {
        Long count = redisTemplate.opsForSet().size(CONNECTED_USERS);
        return count == null ? 0L : count;
    }

    public void registerBuyerFilterAndNotify(String username, BuyerFilterRequest buyerFilterRequest) {
        buyFilterService.saveBuyerFilter(username, buyerFilterRequest);

        List<CardDto> cardDtoList = cardService.findRealtimeCardsByFilter(buyerFilterRequest);

        for (CardDto cardDto : cardDtoList) {
            notificationService.sendMatchingNotification(username, cardDto);
        }
    }

    @Transactional
    public void createRealtimeCardAndNotifyBuyers(String username, CreateRealTimeCardRequest request) {
        CardDto realtimeCard = cardService.createRealtimeCard(username, request);

        //  Redis에서 전체 구매자 필터 key 조회 (예: buyer_filter:{username})
        Set<String> keys = redisTemplate.keys(BUYER_FILTER_PREFIX + "*");
        if (keys.isEmpty()) return;

        for (String key : keys) {
            String filterJson = redisTemplate.opsForValue().get(key);
            if (filterJson == null) continue;

            try {
                BuyerFilterRequest filter = objectMapper.readValue(filterJson, BuyerFilterRequest.class);

                // 조건 매칭 (carrier, dataAmount, priceRange 등)
                if (isMatching(realtimeCard, filter)) {
                    // username 추출 (key: buyer_filter:{username})
                    String buyerUsername = key.substring(BUYER_FILTER_PREFIX.length());
                    notificationService.sendMatchingNotification(buyerUsername, realtimeCard);
                }
            } catch (Exception e) {
                log.warn("[매칭] 구매자 필터 파싱 실패: key={}, err={}", key, e.toString());
            }
        }
    }

    // 조건 비교 함수
    private boolean isMatching(CardDto card, BuyerFilterRequest filter) {
        boolean carrierMatch = card.getCarrier().equals(filter.getCarrier());
        boolean dataMatch = card.getDataAmount().equals(filter.getDataAmount());
        boolean priceMatch = isPriceInRange(card.getPrice(), filter.getPriceRange());
        return carrierMatch && dataMatch && priceMatch;
    }

    private boolean isPriceInRange(Integer price, PriceRange priceRange) {
        Integer min = priceRange.getMin();
        Integer max = priceRange.getMax();
        if (min != null && price < min) return false;
        return max == null || price <= max;
    }
}
