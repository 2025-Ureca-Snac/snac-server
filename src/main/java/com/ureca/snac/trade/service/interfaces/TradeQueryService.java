package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;

import java.util.List;

public interface TradeQueryService {
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

    TradeDto findByTradeId(Long tradeId);

    List<TradeDto> findBuyerRealTimeTrade(String buyerUsername);

    List<TradeDto> findSellerRealTimeTrade(String sellerUsername);
}
