package com.ureca.snac.trade.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.QDispute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class DisputeRepositoryImpl implements DisputeRepositoryCustom {
    private final JPAQueryFactory qf; // 쿼리 dsl 전용 빌더
    private final QDispute qDispute = QDispute.dispute; // dispute 엔티티 q 타입


    @Override
    public Page<Dispute> search(DisputeSearchCond cond, Pageable p) {

        // 동적 where 절을 만드는 booleanBuilder
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (cond.getStatus()!=null) {
            booleanBuilder.and(qDispute.status.eq(cond.getStatus())); // 상태
        }
        if (cond.getType()!=null) {
            booleanBuilder.and(qDispute.type.eq(cond.getType()));
        }
        if (hasText(cond.getReporter())) {
            booleanBuilder.and(qDispute.reporter.email.contains(cond.getReporter()));
        }
        if (cond.getCategory() != null) {
            booleanBuilder.and(qDispute.category.eq(cond.getCategory()));
        }



        List<Dispute> content = qf.selectFrom(qDispute)
                .where(booleanBuilder)
                .orderBy(qDispute.createdAt.desc()) // 최신순
                .offset(p.getOffset())
                .limit(p.getPageSize())
                .fetch();

        long total = qf.select(qDispute.count())
                .from(qDispute)
                .where(booleanBuilder)
                .fetchOne();
        return new PageImpl<>(content, p, total);
    }
}