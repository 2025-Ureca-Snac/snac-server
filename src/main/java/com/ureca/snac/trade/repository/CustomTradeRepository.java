package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Trade;

import java.util.List;

public interface CustomTradeRepository {
    List<Trade> findTradesByBuyerInfinite(Long buyerId, Long lastTradeId, int limit);
    List<Trade> findTradesBySellerInfinite(Long sellerId,  Long lastTradeId, int limit);
}
