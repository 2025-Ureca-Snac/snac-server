package com.ureca.snac.member.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.wallet.exception.WalletAlreadyExistsException;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberJoinEventListener {

    private final MemberRepository memberRepository;
    private final WalletService walletService;
    private final AssetChangedEventFactory assetChangedEventFactory;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;

    private static final long BONUS_AMOUNT = 1000L;

    /**
     * 회원 가입 이벤트를 RabbitMQ 큐를 통해 도입
     * 1. 지갑생성
     * 2. 회원가입 보너스 포인트 및 지급 내역기록
     * 실패시 RabbitMQ 재시도 및 DLQ 매커니즘에 유실 방지
     *
     * @param event 회원가입 이벤트 메시지
     */
    @RabbitListener(queues = RabbitMQConfig.MEMBER_JOIN_QUEUE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMemberJoinEvent(MemberJoinEvent event) {
        Long memberId = event.memberId();
        log.info("[이벤트 수신] 회원가입 메시지 수신 시작. 회원 Id : {}", memberId);

        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            try {
                log.info("[이벤트 수신] 회원 조회 성공. 이메일 : {}", member.getEmail());

                // 지갑 생성 WalletService 위임하여 멱등성 및 트랜잭션 보장
                // 서비스 로직에 예외던지는 로직있어서 재시도 되는 리스너 입장에서는 예외처리안함
                walletService.createWallet(member);
                log.info("[지갑 생성 완료] 회원가입 후처리를 통해 지갑 생성 성공. 회원 ID : {}", memberId);
            } catch (WalletAlreadyExistsException e) {
                // 멱등성 처리
                // 이미 지갑이 존재한다는 예외는 실패가 아니라 이미 처리된 작업임을 의미
                log.warn("[건너뛰기] 이미 지갑이 존재합니다.");
            }

            grantSignupBonusPoint(member);
        } catch (Exception e) {
            log.error("[이벤트 처리 실패] 회원가입 후 처리 오류 발생 회원 ID : {}. 재시도 혹은 DLQ 로 전송",
                    memberId, e);
            // 예외를 다시 던져서 RabbiMQ가 NACK Negative Acknowledgement 로 인지
            // DLQ 동작
            throw e;
        }
    }

    private void grantSignupBonusPoint(Member member) {
        log.info("[포인트 지급] 회원가입 보너스 지급 시작. 회원 ID : {}", member.getId());

        // 1. 서비스에 잔액 변경 요청
        long balanceAfter = walletService.depositPoint(member.getId(), BONUS_AMOUNT);

        // 기록 이벤트 발행
        AssetChangedEvent event = assetChangedEventFactory.createForSignupBonus(
                member.getId(),
                BONUS_AMOUNT,
                balanceAfter
        );
        assetHistoryEventPublisher.publish(event);

        log.info("[포인트 지급] 회원가입 보너스 지급 완료 . 최종 포인트 잔액 : {}", balanceAfter);
    }
}
