package com.ureca.snac.asset.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.ureca.snac.asset.entity.QAssetHistory.assetHistory;
import static com.ureca.snac.member.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetHistoryRepositoryImpl implements AssetHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<AssetHistory> findWithFilters(
            Long memberId, AssetHistoryListRequest request) {

        int pageSize = request.size();

        List<AssetHistory> histories = queryFactory
                .selectFrom(assetHistory)
                .join(assetHistory.member, member).fetchJoin()
                .where(
                        memberIdEq(memberId),
                        assetTypeEq(request.assetType()),
                        transactionTypeEq(request.transactionType()),
                        yearMonthEq(request.yearMonth()),
                        categoryEq(request.category()),
                        cursorCondition(request.cursor())
                )
                .orderBy(assetHistory.createdAt.desc(), assetHistory.id.desc())
                .limit(pageSize + 1) // 다음페이지 확인 위해
                .fetch();

        boolean hasNext = false;
        if (histories.size() > pageSize) {
            histories.remove(pageSize); // 응답 초과분 제거
            hasNext = true;
        }

        return new SliceImpl<>(histories, Pageable.unpaged(), hasNext);
    }

    // 멤버 조건
    private BooleanExpression memberIdEq(Long memberId) {
        return assetHistory.member.id.eq(memberId);
    }

    // AssetType 조건
    private BooleanExpression assetTypeEq(AssetType assetType) {
        return assetType != null ? assetHistory.assetType.eq(assetType) : null;
    }

    /**
     * transactionType 조건 생성
     * 파라미터가 null 이면 쿼리가 무시
     */
    private BooleanExpression transactionTypeEq(TransactionType transactionType) {
        return transactionType != null ?
                assetHistory.transactionType.eq(transactionType) : null;
    }

    // yearMonth 조건 생성
    private BooleanExpression yearMonthEq(String yearMonth) {
        if (yearMonth == null || yearMonth.isBlank()) {
            return null;
        }
        return assetHistory.yearMonth.eq(yearMonth);
    }

    // 카테고리 조건 생성
    private BooleanExpression categoryEq(TransactionCategory category) {
        return category != null ? assetHistory.category.eq(category) : null;
    }

    // 커서 기반 페이징 조건 생성
    private BooleanExpression cursorCondition(String cursorStr) {
        if (cursorStr == null || cursorStr.isBlank()) {
            return null;
        }
        try {
            String[] parts = cursorStr.trim().split(",");
            // StringTokenizer 는 레거시 코드라서 사용안함

            LocalDateTime cursorTime = LocalDateTime.parse(parts[0]);
            Long cursorId = Long.parseLong(parts[1]);

            return assetHistory.createdAt.lt(cursorTime)
                    .or(assetHistory.createdAt.eq(cursorTime).and(assetHistory.id.lt(cursorId)));
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | NumberFormatException exception) {
            log.warn("[커서 파싱 오류] 잘못된 형식의 커서가 입력, 첫페이지로 조회. cursor : {}", cursorStr);
            return null;
        }
    }
}
