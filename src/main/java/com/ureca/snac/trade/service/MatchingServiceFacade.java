package com.ureca.snac.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.controller.request.*;
import com.ureca.snac.trade.dto.CancelTradeDto;
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

    /*-------------------------------------------- 조회 -------------------------------------------- */


    // 실시간 매칭 - 현재 사용자 수 조회
    public long getConnectedUserCount() {
        Long count = redisTemplate.opsForSet().size(CONNECTED_USERS);
        return count == null ? 0L : count;
    }

    // 실시간 매칭 - 구매자 필터 등록 후 필터 조건에 맞는 판매글 알림 수신
    public void registerBuyerFilterAndNotify(String username, BuyerFilterRequest buyerFilterRequest) {
        buyFilterService.saveBuyerFilter(username, buyerFilterRequest);

        List<CardDto> cardDtoList = cardService.findRealtimeCardsByFilter(buyerFilterRequest);

        for (CardDto cardDto : cardDtoList) {
            notificationService.sendMatchingNotification(username, cardDto);
        }
    }

    // 실시간 매칭 - 구매자 필터 조회
    public void getBuyerFilters(String username) {
        Map<String, BuyerFilterRequest> allFilters = buyFilterService.findAllBuyerFilters();
        RetrieveFilterDto dto = new RetrieveFilterDto(username, allFilters);

        notificationService.sendBuyFilterNotification(dto);

        log.info("[필터 발행] username={} filterCount={}", username, allFilters.size());
    }


    /*-------------------------------------------- 실시간 거래 프로세스 -------------------------------------------- */


    // 실시간 매칭 - 판매자 판매글 생성 후 필터 조건에 맞는 구매자에게 해당 정보 전송
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

    // 실시간 매칭 - 판매자에게 거래 수락 요청 -> 이 시점에 Trade 생성 ( Status == BUY_REQUEST, Card == SELLING )
    @Transactional
    public void createTradeFromBuyer(CreateRealTimeTradeRequest createTradeRequest, String username) {
        Long savedId = tradeInitiationService.createRealTimeTrade(createTradeRequest, username);
        TradeDto tradeDto = tradeQueryService.findByTradeId(savedId);
        buyFilterService.deleteBuyerFilterByUsername(username);

        // 판매자에게 알림 전송
        notificationService.notify(tradeDto.getSeller(), tradeDto);
    }

    // 실시간 매칭 - 판매자가 거래 수락 -> ( Status == ACCEPT, Card == TRADING )
    @Transactional
    public void acceptTrade(TradeApproveRequest tradeApproveRequest, String buyerUsername) {
        // 1. 거래 승인 로직 (거래 상태 변경)
        Long tradeId = tradeInitiationService.acceptRealTimeTrade(tradeApproveRequest.getTradeId(), buyerUsername);
        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);
        // 2. 판매자에게 입금 요청 알림 전송
        notificationService.notify(tradeDto.getBuyer(), tradeDto);

        // 3. 연결되지 않는 다른 거래는 자동 취소
        List<TradeDto> trades = tradeProgressService.cancelOtherTradesOfCard(tradeDto.getCardId(), tradeId);

        for (TradeDto dto : trades) {
            CancelTradeDto cancelTradeDto = new CancelTradeDto(dto.getBuyer(), dto);
            notificationService.sendCancelNotification(cancelTradeDto);
        }
    }

    // 실시간 매칭 - 구매자가 금앱을 입금 -> ( Status == PAYMENT_CONFIRM, Card == TRADING )
    @Transactional
    public void payTrade(CreateRealTimeTradePaymentRequest request, String username) {
        // 결제 완료 로직
        Long tradeId = tradeInitiationService.payRealTimeTrade(request, username);
        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 판매자에게 입금 완료 알림 전송
        notificationService.notify(tradeDto.getSeller(), tradeDto);
    }

    // 실시간 매칭 - 판매자가 데이터를 전송 후 해당 증거 사진을 첨부 ( Status == DATA_SENT, Card == TRADING )
    @Transactional
    public void sendTradeData(Long tradeId, MultipartFile file, String username) {
        tradeProgressService.sendTradeData(tradeId, username);
        attachmentService.upload(tradeId, username, file);

        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 구매자에게 확정 요청
        notificationService.notify(tradeDto.getBuyer(), tradeDto);
    }

    // 실시간 매칭 - 구매자가 거래를 확정 ( Status == COMPLETED, Card == SOLD_OUT )
    @Transactional
    public void confirmTrade(ConfirmTradeRequest request, String username) {
        Long tradeId = tradeProgressService.confirmTrade(request.getTradeId(), username);

        TradeDto tradeDto = tradeQueryService.findByTradeId(tradeId);

        // 판매자에게 확정 정보 제공
        notificationService.notify(tradeDto.getSeller(), tradeDto);
    }

    /*-------------------------------------------- 실시간 거래 프로세스 -------------------------------------------- */

    // BUY_REQUEST 구매자 기준 취소
    @Transactional
    public void cancelBuyRequestByBuyer(CancelBuyRequest request, String username) {
        TradeDto tradeDto = tradeProgressService.cancelBuyRequestByBuyerOfCard(request, username);

        notificationService.sendCancelNotification(new CancelTradeDto(tradeDto.getSeller(), tradeDto));
    }

    // BUY_REQUEST 판매자 기준 취소
    @Transactional
    public void cancelBuyRequestBySeller(CancelBuyRequest request, String username) {
        List<TradeDto> trades = tradeProgressService.cancelBuyRequestBySellerOfCard(request, username);

        for (TradeDto tradeDto : trades) {
            notificationService.sendCancelNotification(new CancelTradeDto(tradeDto.getBuyer(), tradeDto));
        }
    }

    // ACCEPTED 거래 취소 - 구매자
    @Transactional
    public void cancelAcceptedTradeByBuyer(CancelRealTimeTradeRequest request, String username) {
        TradeDto tradeDto = tradeProgressService.cancelAcceptedTradeByBuyer(request, username);
        notificationService.sendCancelNotification(new CancelTradeDto(tradeDto.getSeller(), tradeDto));
    }

    // ACCEPTED 거래 취소 - 판매자
    @Transactional
    public void cancelAcceptedTradeBySeller(CancelRealTimeTradeRequest request, String username) {
        TradeDto tradeDto = tradeProgressService.cancelAcceptedTradeBySeller(request, username);
        notificationService.sendCancelNotification(new CancelTradeDto(tradeDto.getBuyer(), tradeDto));
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
