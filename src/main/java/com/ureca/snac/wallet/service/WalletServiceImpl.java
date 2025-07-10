package com.ureca.snac.wallet.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletAlreadyExistsException;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public void createWallet(Member member) {
        walletRepository.findByMemberId(member.getId()).ifPresent(
                wallet -> {
                    // 지갑 예외처리
                    throw new WalletAlreadyExistsException();
                });
        walletRepository.save(Wallet.create(member));
    }

    @Override
    @Transactional
    public void depositMoney(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 변경 엔티티에 위임
        wallet.depositMoney(amount);
    }

    @Override
    @Transactional
    public void withdrawMoney(Long memberId, long amount) {
        // 락걸린 조회 메소드 사용 동시성 제어일단
        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(WalletNotFoundException::new);
        // 잔액 부족도 엔티티가 검증
        wallet.withdrawMoney(amount);
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
}
