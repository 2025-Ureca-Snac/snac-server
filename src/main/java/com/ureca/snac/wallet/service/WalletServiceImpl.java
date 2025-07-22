package com.ureca.snac.wallet.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.dto.WalletSummaryResponse;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletAlreadyExistsException;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

/**
 * WalletService의 구현체는 지갑과 관련된 비즈니스 로직을 수행하는데
 * 아키텍쳐 설계 고려하는 점
 * 템플릿 메소드 패턴을 통해서 입출급 메소드에서 중복되는 지갑조회 및 잠금 로직을
 * 템플릿 메소드로 캡슐화해서 코드 중복을 제거하려고 한다.
 * 더해서 역할과 책임의 분리를 해서 비즈니스 로직은 '무엇을' 이고 템플릿 메소드는 '어떻게 할것인지' 이다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    /**
     * 지갑 조회 및 비관적 락 과 공통 예외 처리
     * 반복적인 코드를 제공
     * 핵심 로직을 함수형 인터페이스로 받는다.
     *
     * @param memberId  조회할 회원의 ID
     * @param operation Wallet 객체를 받아서 작업 수행 하고 반환(람다식)
     * @param <T>       반환될 결과 타입
     * @return operation 결과
     */
    private <T> T executeWithWallet(Long memberId, Function<Wallet, T> operation) {
        log.debug("[지갑 조회] 비관적 락을 사용해서 지갑 조회 시작. 회원 ID : {}", memberId);

        Wallet wallet = walletRepository.findByMemberIdWithLock(memberId)
                .orElseThrow(() -> {
                    log.error("[지갑 조회] 지갑을 찾을 수 없음. 회원 ID : {}", memberId);
                    return new WalletNotFoundException();
                });

        return operation.apply(wallet);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWallet(Member member) {
        log.info("[지갑생성] createWallet 진입 : memberId={}, email={}",
                member.getId(), member.getEmail());

        walletRepository.findByMemberId(member.getId()).ifPresent(
                wallet -> {
                    log.error("[지갑생성] 이미 지갑 존재 memberId={}", member.getId());
                    throw new WalletAlreadyExistsException();
                });

        Wallet wallet = Wallet.create(member);

        walletRepository.save(wallet);
        log.info("[지갑생성] 생성 완료 : walletId={}, memberId={}",
                wallet.getId(), member.getId());
    }

    @Override
    @Transactional
    public Long depositMoney(Long memberId, long amount) {
        log.info("[머니 입금] 시작. 회원 Id : {}, 입금액 : {}", memberId, amount);

        Long finalBalance = executeWithWallet(memberId, wallet -> {
            wallet.depositMoney(amount);
            return wallet.getMoney();
        });
        log.info("[머니 입금] 완료. 회원 ID : {}, 최종 머니 잔액 : {}",
                memberId, finalBalance);
        return finalBalance;
    }

    @Override
    @Transactional
    public Long withdrawMoney(Long memberId, long amount) {
        log.info("[머니 출금] 시작. 회원 Id : {}, 출금액 : {}", memberId, amount);

        Long finalBalance = executeWithWallet(memberId, wallet -> {
            wallet.withdrawMoney(amount);
            return wallet.getMoney();
        });
        log.info("[머니 출금] 완료. 회원 ID : {}, 최종 머니 잔액 : {}",
                memberId, finalBalance);
        return finalBalance;
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
    public Long depositPoint(Long memberId, long amount) {
        log.info("[포인트 적립] 시작. 회원 Id : {}, 적립액 : {}", memberId, amount);

        Long finalBalance = executeWithWallet(memberId, wallet -> {
            wallet.depositPoint(amount);
            return wallet.getPoint();
        });
        log.info("[포인트 적립] 완료. 회원 ID : {}, 최종 포인트 잔액 : {}",
                memberId, finalBalance);
        return finalBalance;
    }

    @Override
    @Transactional
    public Long withdrawPoint(Long memberId, long amount) {
        log.info("[포인트 사용] 시작. 회원 Id : {}, 사용액 : {}", memberId, amount);

        Long finalBalance = executeWithWallet(memberId, wallet -> {
            wallet.withdrawPoint(amount);
            return wallet.getPoint();
        });
        log.info("[포인트 사용] 완료. 회원 ID : {}, 최종 포인트 잔액 : {}",
                memberId, finalBalance);
        return finalBalance;
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

    @Override
    public WalletSummaryResponse getWalletSummary(String email) {
        log.info("[지갑 요약 조회] 시작. 이메일 : {}", email);

        Wallet wallet = walletRepository.findByMemberEmail(email)
                .orElseThrow(() -> {
                    log.error("[지갑 요약 조회] 실패 . 지갑을 찾을 수 없습니다 이메일 : {}", email);
                    return new WalletNotFoundException();
                });

        WalletSummaryResponse response = WalletSummaryResponse.from(wallet);
        log.info("[지갑 요약 조회] 완료. 이메일 : {}", email);

        return response;
    }
}