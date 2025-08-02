package com.ureca.snac.favorite.repository;

import com.ureca.snac.favorite.entity.Favorite;
import com.ureca.snac.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>,
        FavoriteRepositoryCustom {
    /**
     * 특정 단골 관계 존재여부 ID 기반 조회
     *
     * @param fromMemberId 단골을 동록할 회원 ID
     * @param toMemberId   단골로 등록될 회원 ID
     * @return 관계 존재 여부
     */
    boolean existsByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

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
     * 특정 사용자가 등록한 단골 수 조회
     *
     * @param fromMember 단골 목록 조회하고 싶은 회원
     * @return 단골 수
     */
    Long countByFromMember(Member fromMember);

    /**
     * 특정 회원이 주어진 ID 목록에서 단골로 추가한 회원의 ID 만 반환
     * N+1대비
     *
     * @param fromMember  단골 목록의 기준이 되는 회원
     * @param toMemberIds 확인할 상대방 회원 ID 목록
     * @return 주어진 ID 목록중 단골 회원 ID 만 담은 SET
     */
    @Query("""
            select f.toMember.id from Favorite f where f.fromMember = :fromMember
            and f.toMember.id in :toMemberIds
            """)
    Set<Long> findFavoriteToMemberIdsByFromMember(@Param("fromMember") Member fromMember,
                                                  @Param("toMemberIds") List<Long> toMemberIds);
}
