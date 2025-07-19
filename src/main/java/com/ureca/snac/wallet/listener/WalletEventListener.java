package com.ureca.snac.wallet.listener;

import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {
    private final MemberRepository memberRepository;
    private final WalletService walletService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberJoinEvent(MemberJoinEvent event) {
        log.info("[이벤트 수신] 회원가입 : memberId={}", event.memberId());

        Member member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> {
                    log.error("[이벤트 수신] 예외 발생! memberId={}", event.memberId());
                    return new MemberNotFoundException();
                });

        log.info("[이벤트 수신] Member 조회 성공 : {}", member.getEmail());

        walletService.createWallet(member);
        log.info("[이벤트 수신] walletService.createWallet 호출 성공 : memberId={}", member.getId());
    }
    /*
     * 회원가입 이벤트가 발생했을 때 해당 회원의 지갑을 생성
     * 이벤트 트랜잭션활용해서 커밋이 된 이후에만 실제로
     */
    // 리팩토링 근거
    // 서비스 위임 방식인데 WalletEventListener 를 보는 개발자가 createWallet이 트랜잭션으로 동작할 것이라고
    // 추측할 수 밖에없다.. 그래서 의존성이 고려되기 때문에 리스너에서 정리를 할 수 있으면 하는게 좋다 이거지
    // 메인 작업이 커밋된 후에만 실행 == TransactionalEventListener
    // Transactional REQUIRES_NUEW 실행될 때는 독립된 새 트랜잭션으로
    
}
