package com.ureca.snac.favorite.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.favorite.dto.FavoriteListRequest;
import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.ureca.snac.favorite.entity.QFavorite.favorite;
import static com.ureca.snac.member.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryCustomImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final int DEFAULT_SIZE = 10;

    @Override
    public Slice<Favorite> findFavoritesByFromMember(Member fromMember, FavoriteListRequest request) {

        int pageSize = (request.size() == null || request.size() <= 0) ?
                DEFAULT_SIZE : request.size();

        List<Favorite> favoriteList = queryFactory
                .selectFrom(favorite)
                .join(favorite.toMember, member).fetchJoin()
                .where(
                        favorite.fromMember.eq(fromMember),
                        cursorCondition(request.cursor())
                )
                .orderBy(favorite.createdAt.desc(), favorite.id.desc())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (favoriteList.size() > pageSize) {
            favoriteList.remove(pageSize);
            hasNext = true;
        }
        return new SliceImpl<>(favoriteList, Pageable.unpaged(), hasNext);
    }

    // 커서 기반 페이징 조건 생성
    private BooleanExpression cursorCondition(String cursorStr) {
        if (cursorStr == null || cursorStr.isBlank()) {
            return null;
        }
        try {
            String[] parts = cursorStr.trim().split(",");
            // StringTokenizer 는 레거시 코드라서 사용안함
            if (parts.length != 2) {
                log.warn("[커서 파싱 오류] 잘못된 형식의 커서가 입력, 첫 페이지로 조회 cursor : {}",
                        cursorStr);
                return null;
            }
            LocalDateTime cursorTime = LocalDateTime.parse(parts[0]);
            Long cursorId = Long.parseLong(parts[1]);

            return favorite.createdAt.lt(cursorTime)
                    .or(favorite.createdAt.eq(cursorTime).and(favorite.id.lt(cursorId)));
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | NumberFormatException exception) {
            log.warn("[커서 파싱 오류] 잘못된 형식의 커서가 입력, 첫페이지로 조회. cursor : {}", cursorStr);
            return null;
        }
    }
}
