package com.ureca.snac.wallet.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.wallet.dto.WalletSummaryResponse;

public interface WalletService {

    // 지갑 생성
    void createWallet(Member member);

    // 머니 충전
    Long depositMoney(Long memberId, long amount);

    // 머니 출금
    Long withdrawMoney(Long memberId, long amount);

    // 머니 잔액 조회
    long getMoneyBalance(Long memberId);

    // 포인트 입금
    Long depositPoint(Long memberId, long amount);

    // 포인트 출금
    Long withdrawPoint(Long memberId, long amount);

    // 포인트 잔액
    long getPointBalance(Long memberId);

    // 머니와 포인트를 함께 사용하는 출금
    // 복합 출금을 원자성 고려해서 처리
    void withdrawComposite(Long memberId, long moneyAmount, long pointAmount);

    // 특정 회원의 지갑 요약 정보(머니, 포인트 잔액) 조회
    WalletSummaryResponse getWalletSummary(String email);
}