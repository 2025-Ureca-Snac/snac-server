package com.ureca.snac.trade.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.trade.controller.request.TradeQueryType;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.snac.trade.entity.QTrade.trade;

@Repository
@RequiredArgsConstructor
public class TradeRepositoryImpl implements CustomTradeRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Trade> findTradesByBuyerInfinite(Long buyerId, Long lastTradeId, TradeQueryType tradeQueryType, int limit) {
        BooleanExpression predicate = trade.buyer.id.eq(buyerId);

        if (lastTradeId != null) {
            predicate = predicate.and(trade.id.lt(lastTradeId));
        }

        // 거래중 인 거래
        if (tradeQueryType == TradeQueryType.OPEN) {
            predicate = predicate.and(
                    trade.status.notIn(
                            TradeStatus.CANCELED,
                            TradeStatus.COMPLETED,
                            TradeStatus.AUTO_REFUND,
                            TradeStatus.AUTO_PAYOUT
                    )
            );
        }
        if (tradeQueryType == TradeQueryType.CLOSED) {
            predicate = predicate.and(
                    trade.status.in(
                            TradeStatus.CANCELED,
                            TradeStatus.COMPLETED,
                            TradeStatus.AUTO_REFUND,
                            TradeStatus.AUTO_PAYOUT
                    )
            );
        }

        return query.selectFrom(trade)
                .where(predicate)
                .orderBy(trade.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Trade> findTradesBySellerInfinite(Long sellerId, Long lastTradeId, TradeQueryType tradeQueryType, int limit) {
        BooleanExpression predicate = trade.seller.id.eq(sellerId);

        if (lastTradeId != null) {
            predicate = predicate.and(trade.id.lt(lastTradeId));
        }

        // 거래중 인 거래
        if (tradeQueryType == TradeQueryType.OPEN) {
            predicate = predicate.and(
                    trade.status.notIn(
                            TradeStatus.CANCELED,
                            TradeStatus.COMPLETED,
                            TradeStatus.AUTO_REFUND,
                            TradeStatus.AUTO_PAYOUT
                    )
            );
        }
        if (tradeQueryType == TradeQueryType.CLOSED) {
            predicate = predicate.and(
                    trade.status.in(
                            TradeStatus.CANCELED,
                            TradeStatus.COMPLETED,
                            TradeStatus.AUTO_REFUND,
                            TradeStatus.AUTO_PAYOUT
                    )
            );
        }

        return query
                .selectFrom(trade)
                .where(predicate)
                .orderBy(trade.id.desc())
                .limit(limit)
                .fetch();
    }
}
