package com.ureca.snac.favorite.repository;

import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    /**
     * 단골관계 있는지 여부 확인
     *
     * @param fromMember 단골 등록 하는 사람
     * @param toMember   등록 당하는 사람
     * @return 존재 여부
     */
    boolean existsByFromMemberAndToMember(Member fromMember, Member toMember);

    /**
     * 특정 단골 관계 조회
     * 단골 삭제 시 찾아야 삭제함
     *
     * @param findMember 등록한 사람
     * @param toMember   등록당한 사람
     * @return 여부
     */
    Optional<Favorite> findByFromMemberAndToMember(Member findMember, Member toMember);


    /**
     * 최신 등록순 복합 커서 기반 페이지네이션
     * createdAt 내림차랑 id 내림차
     * 만약에 createdAt이 같을경우 에 대한 보험 id
     * 특정 회원이 등록한 단골 목록 조회
     * toMember를 fetch Join 사용
     *
     * @param fromMember      단골 목록 조회하고 싶은 회원
     * @param cursorCreatedAt 이전 페이지 마지막 항목의 생성 시간
     * @param cursorId        이전 페이지의 마지막 항목의 ID
     * @param pageable        페이지 크기정보
     * @return 단골 목록 Slice
     */
    @Query("""
            select f from Favorite f JOIN fetch f.toMember
            where f.fromMember = :fromMember and
            (f.createdAt < :cursorCreatedAt or (f.createdAt = : cursorCreatedAt
            and f.id < :cursorId))
            order by f.createdAt desc, f.id desc
            """)
    Slice<Favorite> findAllWithToMemberByCursor(
            @Param("fromMember") Member fromMember,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable);
}
