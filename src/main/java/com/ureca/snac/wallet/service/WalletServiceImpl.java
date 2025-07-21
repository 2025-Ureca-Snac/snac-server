package com.ureca.snac.wallet.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletAlreadyExistsException;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWallet(Member member) {
        log.info("[지갑생성] createWallet 진입 : memberId={}, email={}", member.getId(), member.getEmail());

        walletRepository.findByMemberId(member.getId()).ifPresent(
                wallet -> {
                    log.error("[지갑생성] 이미 지갑 존재 memberId={}", member.getId());
                    throw new WalletAlreadyExistsException();
                });

        Wallet wallet = Wallet.create(member);

        walletRepository.save(wallet);
        log.info("[지갑생성] 생성 완료 : walletId={}, memberId={}", wallet.getId(), member.getId());
    }

    @Override
    @Transactional
    public Long depositMoney(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 변경 엔티티에 위임
        wallet.depositMoney(amount);

        return wallet.getMoney();
    }

    @Override
    @Transactional
    public Long withdrawMoney(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 부족도 엔티티가 검증
        wallet.withdrawMoney(amount);

        return wallet.getMoney();
    }

    @Override
    public long getMoneyBalance(Long memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 지갑은 반드시 존재 해야되고 잔액반환
        return wallet.getMoney();
    }

    @Override
    @Transactional
    public void depositPoint(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 변경 엔티티에 위임
        wallet.depositPoint(amount);
    }

    @Override
    @Transactional
    public void withdrawPoint(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 변경 엔티티에 위임
        wallet.withdrawPoint(amount);
    }

    @Override
    public long getPointBalance(Long memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 지갑은 반드시 존재 해야되고 잔액반환
        return wallet.getPoint();
    }

    @Override
    @Transactional
    public void withdrawComposite(Long memberId, long moneyAmount, long pointAmount) {
        if (moneyAmount <= 0 && pointAmount <= 0) {
            return;
        }
        log.info("[복합 출금] 시작 . 회원 ID : {}, 머니 : {}, 포인트 : {}",
                memberId, moneyAmount, pointAmount);
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);

        // 트랜잭션 내에서 실행
        if (moneyAmount > 0) {
            wallet.withdrawMoney(moneyAmount);
        }
        if (pointAmount > 0) {
            wallet.withdrawPoint(pointAmount);
        }
        log.info("[복합 출금] 완료 . 최종 머니 : {}, 최종 포인트 : {}",
                wallet.getMoney(), wallet.getPoint());
    }
}


// 지갑 엔드포인트 만들고 private로 리팩토링 빼고