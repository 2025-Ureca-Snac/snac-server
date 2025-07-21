package com.ureca.snac.trade.service;

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

import static com.ureca.snac.common.RedisKeyConstants.CONNECTED_USERS;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketTradeEventListener {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messaging;

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
        // Redis Set에서 제거
        redisTemplate.opsForSet().remove(CONNECTED_USERS, username);
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
