package com.ureca.snac.dev.service;

import com.ureca.snac.dev.dto.DevCancelRechargeRequest;
import com.ureca.snac.dev.dto.DevForceTradeCompleteRequest;
import com.ureca.snac.dev.dto.DevPointGrantRequest;
import com.ureca.snac.dev.dto.DevRechargeRequest;

public interface DevToolService {
    /**
     * 토스 생략 머니 충전
     *
     * @param request 충전 요청 정보
     */
    Long forceRecharge(DevRechargeRequest request);

    /**
     * 포인트 적립 로직
     *
     * @param request 포인트 적립 요청 사유및 금액
     */
    void grantPoint(DevPointGrantRequest request);

    /**
     * 머니 충전 취소 토스 생략
     *
     * @param request 충전 취소 요청 정보
     */
    void forceCancelRecharge(DevCancelRechargeRequest request);

    /**
     * 거래 요청 / 수락 모두 생략 즉시 완료
     *
     * @param request 거래 완료 요청 정보
     * @return 생성된 거래 Id
     */
    Long forceTradeComplete(DevForceTradeCompleteRequest request);
}
