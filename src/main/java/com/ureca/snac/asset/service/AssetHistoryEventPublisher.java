package com.ureca.snac.asset.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 이벤트를 발행하는 클래스
 * 다른 서비스 주입보다 publisher를 주입받아 이벤트를 발행함으로 써,
 * 도메인 간의 의존성을 완벽하게 분리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssetHistoryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 자산 변동 이벤트 발행이라는 단일책임만 수행하는 컴포넌트로
     * 퍼사드 역할을 한다. 우리 시스템의 다른 서비스들이 명확한 의도를가지고 이벤트 발행
     *
     * @param event 발행할 이벤트 객체
     */
    public void publish(AssetChangedEvent event) {
        log.info("[자산 이벤트 발행] 다른 도메인의 요청으로 자산 변경 내역 기록 시작." +
                        "회원 ID : {}, 타입 : {}, 카테고리 : {}, 금액 : {}, 출처 : {}, (출처 ID : {})",
                event.memberId(),
                event.assetType(),
                event.category(),
                event.amount(),
                event.sourceDomain(),
                event.sourceId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BUSINESS_EXCHANGE,
                RabbitMQConfig.ASSET_HISTORY_ROUTING_KEY,
                event
        );
        log.info("[자산 이벤트 발행] 발행 완료. 목적지 Exchange : {}, RoutingKey : {}",
                RabbitMQConfig.BUSINESS_EXCHANGE, RabbitMQConfig.ASSET_HISTORY_ROUTING_KEY);
    }
}
