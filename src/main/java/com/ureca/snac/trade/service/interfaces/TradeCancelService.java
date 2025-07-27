package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.controller.request.CancelBuyRequest;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.entity.CancelReason;

import java.util.List;

public interface TradeCancelService {
    void requestCancel(Long tradeId, String username, CancelReason reason);
    void acceptCancel(Long tradeId, String username);
    void rejectCancel(Long tradeId, String username);

    List<TradeDto> cancelOtherTradesOfCard(Long cardId, Long acceptedTradeId);

    TradeDto cancelBuyRequestByBuyerOfCard(CancelBuyRequest request, String username);

    List<TradeDto> cancelBuyRequestBySellerOfCard(CancelBuyRequest request, String username);

//    TradeDto cancelAcceptedTradeByBuyer(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username);
//
//    TradeDto cancelAcceptedTradeBySeller(CancelRealTimeTradeRequest cancelRealTimeTradeRequest, String username);

    TradeDto cancelRealTimeTrade(Long tradeId, String username, CancelReason reason);
}
