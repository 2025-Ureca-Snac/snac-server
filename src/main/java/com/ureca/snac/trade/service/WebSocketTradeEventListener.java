package com.ureca.snac.trade.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.config.RabbitMQConfig;
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
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ureca.snac.common.RedisKeyConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTradeEventListener {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messaging;
    private final CardService cardService;
    private final RedissonClient redissonClient;
    private final RabbitTemplate rabbitTemplate;

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
            String filterKey = BUYER_FILTER_PREFIX + username;
            if (redisTemplate.hasKey(filterKey)) {
                redisTemplate.delete(filterKey);
                log.info("구매자 필터 삭제: {}", username);
            }

            // 3) DB 카드 삭제
            List<CardDto> cards = cardService.findByMemberUsernameAndSellStatusAndCardCategory(
                    username, SellStatus.SELLING, CardCategory.REALTIME_SELL);
            for (CardDto card : cards) {
                cardService.deleteCard(username, card.getId());
                log.info("판매자 카드 삭제: {} (cardId={})", username, card.getId());
            }

            // 4) 최종 접속자 수 브로드캐스트
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
