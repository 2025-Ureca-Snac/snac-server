package com.ureca.snac.money.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.money.dto.request.MoneyRechargeRequest;
import com.ureca.snac.money.dto.response.MoneyRechargeResponse;

public interface MoneyService {
    MoneyRechargeResponse prepareRecharge(MoneyRechargeRequest request, Member member);

    void processRechargeSuccess(String paymentKey, String orderId, Long amount);
}
// 사용자의 충전 요청을 받아서 PG사와 연동 준비
// 대기 상태의 MoneyRecharge 엔티티 생성 저장


// 토스로부터 결제 완료 응답으로 결제 승인 비즈니스 로직
