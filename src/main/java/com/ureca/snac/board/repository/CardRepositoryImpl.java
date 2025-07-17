package com.ureca.snac.board.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.QCard;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.entity.constants.SellStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CardRepositoryImpl implements CardRepositoryCustom {

    private final JPAQueryFactory query;

    public CardRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Card> scroll(CardCategory cardCategory,
                             Carrier carrier,
                             List<PriceRange> priceRanges,
                             SellStatusFilter sellStatusFilter,
                             int size,
                             Long lastCardId, LocalDateTime lastUpdatedAt) {

        QCard c = QCard.card;

        return query.selectFrom(c)
                .where(
                        c.cardCategory.eq(cardCategory),
                        ltCursor(lastCardId, lastUpdatedAt, c),
                        carrierEq(carrier, c),
                        priceRangeIn(priceRanges, c),
                        sellStatusCond(sellStatusFilter, c)
                )
                .orderBy(c.updatedAt.desc(), c.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression sellStatusCond(SellStatusFilter sellStatusFilter, QCard c) {
        if (sellStatusFilter == null || sellStatusFilter == SellStatusFilter.ALL) {
            return c.sellStatus.ne(SellStatus.PENDING);
        }
        return switch (sellStatusFilter) {
            case SELLING  -> c.sellStatus.eq(SellStatus.SELLING);
            case SOLD_OUT -> c.sellStatus.eq(SellStatus.SOLD_OUT);
            default -> null;
        };
    }

    private BooleanExpression priceRangeIn(List<PriceRange> prList, QCard c) {
        if (prList == null || prList.isEmpty() || prList.contains(PriceRange.ALL)) {
            return null;
        }

        BooleanExpression combined = null;

        for (PriceRange pr : prList) {
            BooleanExpression ge = pr.getMin() != null ? c.price.goe(pr.getMin()) : null;
            BooleanExpression le = pr.getMax() != null ? c.price.loe(pr.getMax()) : null;
            BooleanExpression expr = ge == null ? le : (le == null ? ge : ge.and(le));
            combined = combined == null ? expr : combined.or(expr);
        }

        return combined;
    }

    private BooleanExpression ltCursor(Long lastCardId,
                                       LocalDateTime lastUpdatedAt,
                                       QCard c) {
        if (lastCardId == null || lastUpdatedAt == null) {
            return null;
        }
        return Expressions.booleanTemplate(
                "( {0}, {1} ) < ( {2}, {3} )",
                c.updatedAt, c.id,
                lastUpdatedAt, lastCardId
        );
    }

    private BooleanExpression carrierEq(Carrier carrier, QCard c) {
        return carrier != null ? c.carrier.eq(carrier) : null;
    }

    private BooleanExpression priceRangeEq(PriceRange pr, QCard c) {
        if (pr == null || pr == PriceRange.ALL) {
            return null;
        }
        BooleanExpression ge = pr.getMin() != null ? c.price.goe(pr.getMin()) : null;
        BooleanExpression le = pr.getMax() != null ? c.price.loe(pr.getMax()) : null;
        return ge == null ? le : (le == null ? ge : ge.and(le));
    }
}
