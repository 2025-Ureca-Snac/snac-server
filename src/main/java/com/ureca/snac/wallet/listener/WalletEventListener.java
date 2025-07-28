package com.ureca.snac.wallet.listener;

import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 회원가입 이벤트를 받아서 지갑을 생성하는데
     * 리팩토링 근거
     * WalletService의 지갑 생성에 의종하기때문에 지갑 존재여부, 생성, 저장을 리스너가 다한다
     * 서비스라는 블랙박스 코드를 가정할 필요없다.
     * 트랜잭션 분리 -> 회원가입 트랜잭션 AND 지갑 생성
     * 지갑 생성 실패해도 이미 성공한 회원가입에는 영향 없음
     */

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void handleMemberJoinEvent(MemberJoinEvent event) {
//        log.info("[이벤트 수신] 회원가입 이벤트 수신 : memberId={}", event.memberId());
//
//        // 멤버 조회
//        Member member = memberRepository.findById(event.memberId())
//                .orElseThrow(() -> {
//                    log.error("[지갑 생성 실패] 이벤트를 통해 전달된 memberId로 회원 아이디 찾을 수 없습니다. memberId = {}",
//                            event.memberId());
//                    return new MemberNotFoundException();
//                });
//        log.info("[이벤트 수신] Member 조회 성공 : {}", member.getEmail());
//
//        // 지갑 여부
//        if (walletRepository.findByMemberId(member.getId()).isPresent()) {
//            log.warn("[지갑 생성 건너뜀] 이미 해당 회원의 지갑이 존재합니다. memberId : {}",
//                    member.getId());
//            return;
//        }
//
//        Wallet wallet = Wallet.create(member);
//        walletRepository.save(wallet);
//
//        log.info("[지갑 생성 완료] 회원가입 이벤트를 통해 지갑 생성 완료. memberId : {}, walletId : {}",
//                member.getId(), wallet.getId());
//    }
    /*
     * 회원가입 이벤트가 발생했을 때 해당 회원의 지갑을 생성
     * 이벤트 트랜잭션활용해서 커밋이 된 이후에만 실제로
     */


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberJoinEvent(MemberJoinEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.BUSINESS_EXCHANGE, RabbitMQConfig.MEMBER_JOIN_ROUTING_KEY, event.memberId());
    }

    @RabbitListener(queues = RabbitMQConfig.MEMBER_JOIN_QUEUE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMemberJoinListener(Long memberId) {
        log.info("[이벤트 수신] 회원가입 이벤트 수신 : memberId={}", memberId);

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("[지갑 생성 실패] 이벤트를 통해 전달된 memberId로 회원 아이디 찾을 수 없습니다. memberId = {}",
                            memberId);
                    return new MemberNotFoundException();
                });
        log.info("[이벤트 수신] Member 조회 성공 : {}", member.getEmail());

        // 지갑 여부
        if (walletRepository.findByMemberId(member.getId()).isPresent()) {
            log.warn("[지갑 생성 건너뜀] 이미 해당 회원의 지갑이 존재합니다. memberId : {}",
                    member.getId());
            return;
        }

        Wallet wallet = Wallet.create(member);
        walletRepository.save(wallet);

        log.info("[지갑 생성 완료] 회원가입 이벤트를 통해 지갑 생성 완료. memberId : {}, walletId : {}",
                member.getId(), wallet.getId());
    }
}
