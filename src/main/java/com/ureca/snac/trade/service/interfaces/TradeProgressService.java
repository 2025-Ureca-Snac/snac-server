package com.ureca.snac.trade.service.interfaces;


public interface TradeProgressService {

    /**
     * 진행 중인 거래를 취소하고 환불을 처리합니다.
     *
     * @param tradeId  취소할 거래 ID
     * @param username 요청 회원의 이메일
     */
    Long sendTradeData(Long tradeId, String username);

    /**
     * 판매자가 거래 데이터를 전송합니다.
     *
     * @param tradeId  거래 ID
     * @param username 요청 판매자 이메일
     */
    Long confirmTrade(Long tradeId, String username, Boolean hasCard);

//    List<TradeDto> cancelOtherTradesOfCard(Long cardId, Long acceptedTradeId);
//
//    TradeDto cancelBuyRequestByBuyerOfCard(CancelBuyRequest request, String username);
//
//    List<TradeDto> cancelBuyRequestBySellerOfCard(CancelBuyRequest request, String username);
//
//    TradeDto cancelAcceptedTradeByBuyer(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username);
//
//    TradeDto cancelAcceptedTradeBySeller(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username);
//
//    TradeDto cancelRealTimeTrade(Long tradeId, String username, CancelReason reason);
//
//    TradeDto cancelRealTimeTradeWithRefund(Long tradeId, String username);
}
