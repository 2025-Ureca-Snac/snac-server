package com.ureca.snac.settlement.application.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.settlement.domain.entity.Settlement;
import com.ureca.snac.settlement.domain.repository.SettlementRepository;
import com.ureca.snac.settlement.domain.service.SettlementValidator;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
// 어댑터 (구현체)
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final MemberRepository memberRepository;
    private final WalletService walletService;

    // 검증 내부 서비스 도메인
    private final SettlementValidator settlementValidator;
    // 팩토리랑 퍼블리셔 주입
    private final AssetChangedEventFactory assetChangedEventFactory;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;

    /**
     * 트랜 잭션 관리 책임 서비스
     *
     * @param username      정산 요청 회원
     * @param amount        정산 금액
     * @param accountNumber 입금받을 계좌번호
     */
    @Override
    @Transactional
    public void processSettlement(String username, long amount, String accountNumber) {
        log.info("[정산 처리] 시작. 사용자 : {}", username);

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(MemberNotFoundException::new);

        // 유효성 검증
        settlementValidator.validate(member, accountNumber);
        log.info("[정산 처리] 계좌 번호 검증 완료 . 회원 ID : {}", member.getId());

        // 정산 기록생성
        Settlement settlement = Settlement.create(member, amount);
        settlementRepository.save(settlement);
        log.info("[정산 처리] 정산 에티티 생성 완료. 정산 ID : {}", settlement.getId());

        Long balanceAfter = walletService.withdrawMoney(member.getId(), amount);
        log.info("[정산 처리] 머니 출금 완료. 회원 ID : {}, 잔액 : {}",
                member.getId(), balanceAfter);

        AssetChangedEvent event = assetChangedEventFactory.createForSettlement(
                member.getId(),
                settlement.getId(),
                amount,
                balanceAfter
        );

        assetHistoryEventPublisher.publish(event);
        log.info("[정산 처리] 자산 병동 이벤트 발행 완료. 회원 ID : {}", member.getId());
    }
}
