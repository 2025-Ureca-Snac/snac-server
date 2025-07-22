package com.ureca.snac.trade.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.trade.entity.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.snac.trade.entity.QTrade.trade;

@Repository
@RequiredArgsConstructor
public class TradeRepositoryImpl implements CustomTradeRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Trade> findTradesByBuyerInfinite(Long buyerId, Long lastTradeId, int limit) {
        BooleanExpression predicate = trade.buyer.id.eq(buyerId);

        if (lastTradeId != null) {
            predicate = predicate.and(trade.id.lt(lastTradeId));
        }

        return query.selectFrom(trade)
                .where(predicate)
                .orderBy(trade.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Trade> findTradesBySellerInfinite(Long sellerId, Long lastTradeId, int limit) {
        BooleanExpression predicate = trade.seller.id.eq(sellerId);

        if (lastTradeId != null) {
            predicate = predicate.and(trade.id.lt(lastTradeId));
        }
        return query
                .selectFrom(trade)
                .where(predicate)
                .orderBy(trade.id.desc())
                .limit(limit)
                .fetch();
    }
}
