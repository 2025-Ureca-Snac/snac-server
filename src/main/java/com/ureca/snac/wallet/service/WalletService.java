package com.ureca.snac.wallet.service;

import com.ureca.snac.member.Member;

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
    void depositPoint(Long memberId, long amount);

    // 포인트 출금
    void withdrawPoint(Long memberId, long amount);

    // 포인트 잔액
    long getPointBalance(Long memberId);
}