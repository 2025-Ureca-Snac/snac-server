package com.ureca.snac.config;

import com.ureca.snac.auth.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ureca.snac.common.RedisKeyConstants.WS_CONNECTED_PREFIX;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebConfigRabbitMQ implements WebSocketMessageBrokerConfigurer {

    private final JWTUtil jwtUtil;
    private final StompRelayProperties stompProps;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/queue", "/topic");

        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(stompProps.getHost())
                .setRelayPort(stompProps.getPort())
                .setClientLogin(stompProps.getClientLogin())
                .setClientPasscode(stompProps.getClientPasscode())
                .setSystemLogin(stompProps.getSystemLogin())
                .setSystemPasscode(stompProps.getSystemPasscode());

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor
                        .getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        String email = jwtUtil.getUsername(token);

                        // [중복 접속 체크 & 등록]
                        String redisKey = WS_CONNECTED_PREFIX + email;
                        Boolean already = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.DAYS); // 1일 TTL

                        // 이미 소켓 연결 중인 상태 (중복)
                        if (Boolean.FALSE.equals(already)) {
                            throw new IllegalStateException("이미 소켓 연결 중입니다.");
                        }

                        Authentication user = new UsernamePasswordAuthenticationToken(
                                email, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        accessor.setUser(user);
                    }
                }
                return message;
            }
        });
    }
}
