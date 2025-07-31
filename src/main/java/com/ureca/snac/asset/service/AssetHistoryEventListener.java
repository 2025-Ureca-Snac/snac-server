package com.ureca.snac.asset.service;

import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.repository.AssetHistoryRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetHistoryEventListener {

    private final AssetHistoryRepository assetHistoryRepository;
    private final MemberRepository memberRepository;

    /**
     * 트랜잭션 이벤트 리스너를 통해서
     * 이벤트 발행한 쪽의 트랜잭션이 커밋될 때만 실행. 데이터 정합성 보장
     * REQUIRES_NEW 를 통해 새 트랜잭션에서 실행하여,
     * 내역 기록이 실패하더라도 원래 트랜잭션에 영향주지 않도록 결리
     *
     * @param event 자산 변동 정보 이벤트
     */

    @TransactionalEventListener // 언제 실행할지 메인작업 커밋후
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새 트랜잭션으로
    public void handleAssetChangedEvent(AssetChangedEvent event) {
        log.info("[자산 이벤트 수신] 자산 내역 기록 시작. 회원 ID : {}", event.memberId());

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
            log.info("[자산 내역 기록 완료] 기록 저장 성공. historyId : {}, title : {}", history.getId(), history.getTitle());
        } catch (Exception e) {
            // 예외 다시 던져서 트랜잭션 롤백되게
            log.error("[자산 내역 기록 실패] 기록 중 심각한 예외 발생. 트랜잭션을 롤백 memberId : {}, event : {}",
                    event.memberId(), event, e);
            throw e;
        }
    }
}
