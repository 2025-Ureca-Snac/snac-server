package com.ureca.snac.trade.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.ureca.snac.common.RedisKeyConstants.BUYER_FILTER_PREFIX;
import static com.ureca.snac.common.RedisKeyConstants.CONNECTED_USERS;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTradeEventListener {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messaging;
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

        // 1. 접속자 목록에서 제거
        redisTemplate.opsForSet().remove(CONNECTED_USERS, username);

        // 2. 필터링 조건 Redis 에서 제거 (구매자 역할일 수 있음)
        String filterKey = BUYER_FILTER_PREFIX + username;
        if (redisTemplate.hasKey(filterKey)) {
            redisTemplate.delete(filterKey);
            log.info("구매자 필터링 조건 삭제: {}", username);
        }

        // 3. DB에서 카드 조회 후 조건에 따라 제거 (판매자 역할일 수 있음)
        List<CardDto> cards = cardService.findByMemberUsernameAndSellStatusAndCardCategory(
                username,
                SellStatus.SELLING,
                CardCategory.REALTIME_SELL
        );

        for (CardDto card : cards) {
            cardService.deleteCard(username, card.getId());
            log.info("판매자 카드 삭제 완료: {}", username);
        }

        broadcastUserCount();
    }

    private void broadcastUserCount() {
        Long count = redisTemplate.opsForSet().size(CONNECTED_USERS);
        messaging.convertAndSend("/topic/connected-users", count == null ? 0 : count);
    }

    // username 추출
    private String extractUsername(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        return (principal != null) ? principal.getName() : null;
    }
}
