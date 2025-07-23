package com.ureca.snac.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.controller.request.*;
import com.ureca.snac.trade.dto.RetrieveFilterDto;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
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
    private final TradeInitiationService tradeInitiationService;
    private final TradeQueryService tradeQueryService;
    private final TradeProgressService tradeProgressService;
    private final BuyFilterService buyFilterService;
    private final AttachmentService attachmentService;

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

    public void getBuyerFilters(String username) {
        Map<String, BuyerFilterRequest> allFilters = buyFilterService.findAllBuyerFilters();
        RetrieveFilterDto dto = new RetrieveFilterDto(username, allFilters);

        notificationService.sendBuyFilterNotification(dto);

        log.info("[필터 발행] username={} filterCount={}", username, allFilters.size());
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
                if (isMatching(realtimeCard, filter) && filter.getActive()) {
                    // username 추출 (key: buyer_filter:{username})
                    String buyerUsername = key.substring(BUYER_FILTER_PREFIX.length());
                    notificationService.sendMatchingNotification(buyerUsername, realtimeCard);
                }
            } catch (Exception e) {
                log.warn("[매칭] 구매자 필터 파싱 실패: key={}, err={}", key, e.toString());
            }
        }
    }

    // 판매자에게 거래 수락 요청 -> 이 시점에 Trade 생성 ( Status == BUY_REQUEST )
    @Transactional
    public void createTradeFromBuyer(CreateRealTimeTradeRequest createTradeRequest, String username) {
        Long savedId = tradeInitiationService.createRealTimeTrade(createTradeRequest, username);
        TradeDto tradeDto = tradeQueryService.findByTradeId(savedId);
        buyFilterService.deleteBuyerFilterByUsername(username);

        // 판매자에게 알림 전송
        notificationService.notify(tradeDto.getSeller(), tradeDto);
    }

    // 판매자가 거래 수락 -> ( Status == ACCEPT )
    @Transactional
    public void acceptTrade(TradeApproveRequest tradeApproveRequest, String buyerUsername) {
        // 1. 거래 승인 로직 (거래 상태 변경)
        Long tradeId = tradeInitiationService.acceptTrade(tradeApproveRequest.getTradeId(), buyerUsername);
        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);
        // 2. 판매자에게 입금 요청 알림 전송
        notificationService.notify(tradeDto.getBuyer(), tradeDto);
    }

    @Transactional
    public void payTrade(CreateRealTimeTradePaymentRequest request, String username) {
        // 결제 완료 로직
        Long tradeId = tradeInitiationService.payTrade(request, username);
        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 판매자에게 입금 완료 알림 전송
        notificationService.notify(tradeDto.getSeller(), tradeDto);
    }

    @Transactional
    public void sendTradeData(Long tradeId, MultipartFile file, String username) {
        tradeProgressService.sendTradeData(tradeId, username);
        attachmentService.upload(tradeId, username, file);

        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 구매자에게 확정 요청
        notificationService.notify(tradeDto.getBuyer(), tradeDto);
    }

    @Transactional
    public void confirmTrade(ConfirmTradeRequest request, String username) {
        Long tradeId = tradeProgressService.confirmTrade(request.getTradeId(), username);

        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 판매자에게 확정 정보 제공
        notificationService.notify(tradeDto.getSeller(), tradeDto);
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
