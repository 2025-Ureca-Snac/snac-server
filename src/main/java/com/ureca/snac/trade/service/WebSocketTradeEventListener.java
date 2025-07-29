package com.ureca.snac.trade.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.notification.service.NotificationService;
import com.ureca.snac.trade.dto.CancelTradeDto;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.service.interfaces.BuyFilterService;
import com.ureca.snac.trade.service.interfaces.TradeCancelService;
import com.ureca.snac.trade.service.interfaces.TradeProgressService;
import com.ureca.snac.trade.service.interfaces.TradeQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ureca.snac.board.entity.constants.CardCategory.REALTIME_SELL;
import static com.ureca.snac.board.entity.constants.SellStatus.SELLING;
import static com.ureca.snac.board.entity.constants.SellStatus.TRADING;
import static com.ureca.snac.common.RedisKeyConstants.CONNECTED_USERS;
import static com.ureca.snac.common.RedisKeyConstants.WS_DISCONNECT_LOCK_PREFIX;
import static com.ureca.snac.trade.entity.CancelReason.BUYER_FORCED_TERMINATION;
import static com.ureca.snac.trade.entity.CancelReason.SELLER_FORCED_TERMINATION;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTradeEventListener {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messaging;
    private final RabbitTemplate rabbitTemplate;
    private final RedissonClient redissonClient;

    private final NotificationService notificationService;
    private final TradeProgressService tradeProgressService;
    private final TradeQueryService tradeQueryService;
    private final TradeCancelService tradeCancelService;
    private final BuyFilterService buyFilterService;
    private final CardService cardService;

    // 소켓 연결시 호출
    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        String username = extractUsername(event);
        if (username == null) return;

        // Redis Set에 추가
        redisTemplate.opsForSet().add(CONNECTED_USERS, username);

        // 브로드 캐스트
        broadcastUserCount();
    }

    // 소켓 해제시 호출
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String username = extractUsername(event);
        if (username == null) return;

        // 분산락: 사용자별 고유 키로 락 획득
        String lockKey = WS_DISCONNECT_LOCK_PREFIX + username;
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;

        try {
            // 최대 5초 대기, 획득 시 10초 TTL
            acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Disconnect 처리용 락 획득 실패, 건너뜀: {}", username);
                return;
            }

            // 1) 접속자 목록에서 제거
            redisTemplate.opsForSet().remove(CONNECTED_USERS, username);

            // 2) 필터 조건 삭제
            buyFilterService.deleteBuyerFilterByUsername(username);

            // 3) 강제 종료 처리
            forceCancelRealTimeTrades(username);

            // 4) DB 카드 삭제
            List<CardDto> cards = cardService.findByMemberUsernameAndSellStatusesAndCardCategory(username, List.of(SELLING, TRADING), REALTIME_SELL);
            for (CardDto card : cards) {
                cardService.deleteCardByRealTime(username, card.getCardId());
                log.info("판매자 카드 삭제: {} (cardId={})", username, card.getCardId());
            }

            // 5) 최종 접속자 수 브로드캐스트
            broadcastUserCount();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("락 대기 중 인터럽트 발생: {}", username, e);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    /** RealTime 거래 중 Buyer/Seller 관점에서 active trades 강제 종료 */
    private void forceCancelRealTimeTrades(String username) {

        TradeDto dataSentTradeDto = tradeQueryService.onBuyerDataSentRealTime(username);

        if (dataSentTradeDto != null) {
            TradeDto confrimTradeDto = tradeProgressService.confirmTrade(dataSentTradeDto.getTradeId(), username, false);
            notificationService.notify(confrimTradeDto.getSeller(), confrimTradeDto);
        }

        // 구매자 관점 거래
        List<TradeDto> buyerTrades = tradeQueryService.findBuyerRealTimeTrade(username);
        for (TradeDto trade : buyerTrades) {
            if (isCancellable(trade.getStatus())) {
                TradeDto tradeDto = tradeCancelService.cancelRealTimeTrade(
                        trade.getTradeId(),
                        username,
                        BUYER_FORCED_TERMINATION);


                log.info("강제종료(구매자): username={} tradeId={}", username, tradeDto.getTradeId());

                CancelTradeDto cancelTradeDto = new CancelTradeDto(tradeDto.getSeller(), tradeDto);

                notificationService.sendCancelNotification(cancelTradeDto);
            }
        }

        // 판매자 관점 거래
        List<TradeDto> sellerTrades = tradeQueryService.findSellerRealTimeTrade(username);
        for (TradeDto trade : sellerTrades) {
            if (isCancellable(trade.getStatus())) {
                TradeDto tradeDto = tradeCancelService.cancelRealTimeTrade(
                        trade.getTradeId(),
                        username,
                        SELLER_FORCED_TERMINATION
                );

                log.info("강제종료(판매자): username={} tradeId={}", username, trade.getTradeId());

                CancelTradeDto cancelTradeDto = new CancelTradeDto(tradeDto.getBuyer(), tradeDto);

                notificationService.sendCancelNotification(cancelTradeDto);
            }
        }
    }

    private boolean isCancellable(TradeStatus status) {
        return switch (status) {
            case BUY_REQUESTED, ACCEPTED, PAYMENT_CONFIRMED -> true;
            default -> false;
        };
    }

    private void broadcastUserCount() {
        Long count = redisTemplate.opsForSet().size(CONNECTED_USERS);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONNECTED_USERS_EXCHANGE,
                "",                                      // Fanout: 라우팅키 필요 없음
                count == null ? 0 : count
        );
    }

    // username 추출
    private String extractUsername(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        return (principal != null) ? principal.getName() : null;
    }
}
