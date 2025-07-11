package com.ureca.snac.wallet.listener;

import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {
    private final WalletService walletService;

    @TransactionalEventListener
    public void handleMemberJoinEvent(MemberJoinEvent event) {
        log.info("신규 회원가입 시 이벤트 수신해서 지갑 생성함 {}", event.member().getEmail());
        walletService.createWallet(event.member());
    }
    /**
     * 회원가입 이벤트가 발생했을 때 해당 회원의 지갑을 생성
     * 이벤트 트랜잭션활용해서 커밋이 된 이후에만 실제로
     */
}
