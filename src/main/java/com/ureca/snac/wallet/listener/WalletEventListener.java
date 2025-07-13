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
}
