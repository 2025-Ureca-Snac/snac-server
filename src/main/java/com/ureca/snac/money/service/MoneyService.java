package com.ureca.snac.money.service;

import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;

public interface MoneyService {
    /**
     * 머니 충전 서비스
     *
     * @param request 요청 DTO
     * @param email   사용자 식별
     * @return 결제 준비 DTO
     */
    MoneyRechargeResponse prepareRecharge(MoneyRechargeRequest request, String email);

    // 토스로부터 성공 콜백 받아서 머니 충전 처리
    void processRechargeSuccess(String paymentKey, String orderId, Long amount, String email);
}

