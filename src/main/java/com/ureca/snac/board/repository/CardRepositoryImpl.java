package com.ureca.snac.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.board.controller.request.SellStatusFilter;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.QCard;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.favorite.entity.QFavorite;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.entity.QMember;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.ureca.snac.board.entity.constants.CardCategory.*;

@Repository
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Card> scroll(CardCategory cardCategory,
                             Carrier carrier,
                             PriceRange priceRange,
                             SellStatusFilter sellStatusFilter,
                             Boolean highRatingFirst,
                             Integer size,
                             Long lastCardId,
                             LocalDateTime lastUpdatedAt,
                             Boolean favoriteOnly,
                             String username
    ) {

        QCard c = QCard.card;
        QMember m = QMember.member;

        JPAQuery<Card> q = query
                .selectFrom(c)
                .join(c.member, m).fetchJoin()
                .where(
                        c.cardCategory.ne(CardCategory.REALTIME_SELL),
                        c.cardCategory.eq(cardCategory),
                        ltCursor(lastCardId, lastUpdatedAt, c),
                        carrierEq(carrier, c),
                        priceRangeEq(priceRange, c),
                        sellStatusCond(sellStatusFilter, c)
                );

        // 단골 필터링이 트루일때 동적으로 추가
        if (Boolean.TRUE.equals(favoriteOnly) && username != null) {
            QFavorite f = QFavorite.favorite;
            q.join(f).on(c.member.eq(f.toMember))
                    // 카드의 작성자와 단골 대상을 조인
                    .where(f.fromMember.email.eq(username));
            // 단골을 추가한 사람이 현재 사용자
        }

        if (highRatingFirst) {
            q.orderBy(m.ratingScore.desc(), c.updatedAt.desc(), c.id.desc());
        } else {
            q.orderBy(c.updatedAt.desc(), c.id.desc());
        }

        return q.limit(size).fetch();
    }

    public List<Card> scrollByOwnerAndCategory(Member member, CardCategory category, int size, Long lastCardId, LocalDateTime lastUpdatedAt) {
        QCard c = QCard.card;

        return query
                .selectFrom(c)
                .where(
                        c.member.eq(member),
                        c.cardCategory.eq(category),
                        ltCursor(lastCardId, lastUpdatedAt, c)
                )
                .orderBy(c.updatedAt.desc(), c.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Card> findRealtimeCardsByFilter(BuyerFilterRequest filter) {
        QCard card = QCard.card;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(card.sellStatus.eq(SellStatus.SELLING));
        builder.and(card.cardCategory.eq(REALTIME_SELL));
        builder.and(card.carrier.eq(filter.getCarrier()));
        builder.and(card.dataAmount.eq(filter.getDataAmount()));

//        Integer min = filter.getPriceRange().getMin();
//        Integer max = filter.getPriceRange().getMax();
//
//        if (min != null) builder.and(card.price.goe(min));
//        if (max != null) builder.and(card.price.loe(max));

        Integer max = filter.getPriceRange().getMax();

        // 항상 0원 이상
        builder.and(card.price.goe(0));
        if (max != null) {
            builder.and(card.price.loe(max));
        }

        return query.selectFrom(card)
                .where(builder)
                .fetch();
    }

    private BooleanExpression sellStatusCond(SellStatusFilter sellStatusFilter, QCard c) {
        if (sellStatusFilter == null || sellStatusFilter == SellStatusFilter.ALL) {
            return c.sellStatus.ne(SellStatus.PENDING);
        }
        return switch (sellStatusFilter) {
            case SELLING -> c.sellStatus.eq(SellStatus.SELLING);
            case SOLD_OUT -> c.sellStatus.eq(SellStatus.SOLD_OUT);
            default -> null;
        };
    }

//    private BooleanExpression priceRangeIn(List<PriceRange> prList, QCard c) {
//        if (prList == null || prList.isEmpty() || prList.contains(PriceRange.ALL)) {
//            return null;
//        }
//
//        BooleanExpression combined = null;
//
//        for (PriceRange pr : prList) {
//            BooleanExpression ge = pr.getMin() != null ? c.price.goe(pr.getMin()) : null;
//            BooleanExpression le = pr.getMax() != null ? c.price.loe(pr.getMax()) : null;
//            BooleanExpression expr = ge == null ? le : (le == null ? ge : ge.and(le));
//            combined = combined == null ? expr : combined.or(expr);
//        }
//
//        return combined;
//    }

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
        // 항상 0원 이상, max만 제한
        return c.price.goe(0).and(c.price.loe(pr.getMax()));
    }
}
