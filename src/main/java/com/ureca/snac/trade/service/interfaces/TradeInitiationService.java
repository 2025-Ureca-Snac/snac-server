package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateRealTimeTradePaymentRequest;
import com.ureca.snac.trade.controller.request.CreateRealTimeTradeRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;

public interface TradeInitiationService {
    /**
     * 판매 거래 요청을 생성합니다.
     *
     * @param createTradeRequest 거래 생성 정보
     * @param username 요청 회원의 이메일
     * @return 생성된 거래 ID
     */
    Long createSellTrade(CreateTradeRequest createTradeRequest, String username);

    /**
     * 구매 거래 요청을 생성합니다.
     *
     * @param createTradeRequest 거래 생성 정보
     * @param username 요청 회원의 이메일
     * @return 생성된 거래 ID
     */
    Long createBuyTrade(CreateTradeRequest createTradeRequest, String username);

    /**
     * 구매글에 판매자가 거래 신청을 하고, 카드 상태를 TRADING으로 변경합니다.
     *
     * @param claimBuyRequest 구매글을 식별할 카드 ID를 포함한 DTO
     * @param username        거래 신청을 수행하는 판매자 이메일
     */
    Long acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username);

    Long createRealTimeTrade(CreateRealTimeTradeRequest request, String username);

    Long acceptTrade(Long tradeId, String username);

    Long payTrade(CreateRealTimeTradePaymentRequest createRealTimeTradePaymentRequest, String username);
}
