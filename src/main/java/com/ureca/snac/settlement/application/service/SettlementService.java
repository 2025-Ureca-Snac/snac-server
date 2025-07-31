package com.ureca.snac.settlement.application.service;

// 포트 (인터페이스)
public interface SettlementService {

    /**
     * 정산 요청 처리
     *
     * @param username      정산 요청 회원
     * @param amount        정산 금액
     * @param accountNumber 입금받을 계좌번호
     */
    void processSettlement(String username, long amount, String accountNumber);
}
