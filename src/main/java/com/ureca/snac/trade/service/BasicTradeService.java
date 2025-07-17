package com.ureca.snac.trade.service;

import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BasicTradeService {
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
     * 진행 중인 거래를 취소하고 환불을 처리합니다.
     *
     * @param tradeId 취소할 거래 ID
     * @param username 요청 회원의 이메일
     */
    void cancelTrade(Long tradeId, String username);

    /**
     * 판매자가 거래 데이터를 전송합니다.
     *
     * @param tradeId 거래 ID
     * @param username 요청 판매자 이메일
     * @param picture 업로드 파일
     */
    void sendTradeData(Long tradeId, String username, MultipartFile picture);

    /**
     * 구매자가 거래를 확정합니다.
     *
     * @param tradeId 거래 ID
     * @param username 요청 구매자 이메일
     */
    void confirmTrade(Long tradeId, String username);

    /**
     * 무한 스크롤 방식으로 거래 내역을 조회합니다.
     *
     * @param username 조회 회원 이메일
     * @param side 조회 관점 (BUY/SELL)
     * @param size 조회 건수
     * @param lastTradeId 커서용 마지막 거래 ID
     * @return 거래 목록 및 다음 페이지 정보
     */
    ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, Long lastTradeId);

    /**
     * 판매자 관점 진행 중 거래 수를 조회합니다.
     *
     * @param username 조회 판매자 이메일
     * @return 진행 중 거래 건수
     */
    ProgressTradeCountResponse countSellingProgress(String username);

    /**
     * 구매자 관점 진행 중 거래 수를 조회합니다.
     *
     * @param username 조회 구매자 이메일
     * @return 진행 중 거래 건수
     */
    ProgressTradeCountResponse countBuyingProgress(String username);

    /**
     * 구매글에 판매자가 거래 신청을 하고, 카드 상태를 TRADING으로 변경합니다.
     *
     * @param claimBuyRequest 구매글을 식별할 카드 ID를 포함한 DTO
     * @param username        거래 신청을 수행하는 판매자 이메일
     */
    void acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username);
}
