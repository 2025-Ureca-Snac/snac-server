package com.ureca.snac.asset.service;

import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.repository.AssetHistoryRepository;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!scheduler")
@RequiredArgsConstructor
public class AssetHistoryEventListener {

    private final AssetHistoryRepository assetHistoryRepository;
    private final MemberRepository memberRepository;

    /**
     * RabbitMQ 를 통해 자산 변동 이벤트
     * 이벤트 발행 트랜잭션과 분리되어서 비동기 처리 보장
     * 새 트랜잭션 발행
     * 내역 기록이 실패해도 원래 트랜잭션에 영향 X
     * 실패 시 RabbitMQ 의 재시도 및 DLQ 매커니즘 사용
     *
     * @param event 자산 변동 정보 이벤트
     */
    @RabbitListener(queues = RabbitMQConfig.ASSET_HISTORY_QUEUE)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새 트랜잭션으로
    public void handleAssetChangedEvent(AssetChangedEvent event) {
        log.info("[자산 이벤트 수신] RabbitMQ 를 통해 자산 내역 기록 시작. 회원 ID : {}", event.memberId());

        try {
            Member member = memberRepository.findById(event.memberId())
                    .orElseThrow(MemberNotFoundException::new);

            AssetHistory history = AssetHistory.create(
                    member,
                    event.assetType(),
                    event.transactionType(),
                    event.category(),
                    event.amount(),
                    event.balanceAfter(),
                    event.title(),
                    event.sourceDomain(),
                    event.sourceId()
            );

            assetHistoryRepository.save(history);
            log.info("[자산 내역 기록 완료] 기록 저장 성공. historyId : {}, title : {}",
                    history.getId(), history.getTitle());
        } catch (Exception e) {
            // 예외 다시 던져서 트랜잭션 롤백되게
            log.error("[자산 내역 기록 실패] 기록 중 심각한 예외 발생. RabbitMQ가 재시도 하거나 DLQ 로 보냄. 회원 Id : {}, event : {}",
                    event.memberId(), event, e);
            throw e;
        }
    }
}
