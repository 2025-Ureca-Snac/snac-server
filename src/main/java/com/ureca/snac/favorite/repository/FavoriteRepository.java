package com.ureca.snac.favorite.repository;

import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     * 특정 회원이 등록한 단골 목록 조회
     * toMember를 fetch Join 사용
     *
     * @param fromMember 단골 목록 조회하고 싶은 회원
     * @return 단골 목록 List
     */
    @Query("""
            select f from Favorite f JOIN fetch f.toMember
            where f.fromMember = :fromMember and f.id < :cursorId
            order by f.createdAt desc
            """)
    Slice<Favorite> findAllWithToMemberByCursor(
            @Param("fromMember") Member fromMember,
            @Param("cursorId") Long cursorId,
            Pageable pageable);
}
