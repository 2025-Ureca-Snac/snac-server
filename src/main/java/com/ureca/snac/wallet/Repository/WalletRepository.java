package com.ureca.snac.wallet.Repository;

import com.ureca.snac.wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByMemberId(Long memberId);

    // 동시성 제어 하기위해서 락 써야됨
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.member.id= :memberId")
    Optional<Wallet> findByMemberIdWithLock(@Param("memberId") Long memberId);

    /**
     * 이메일을 기준으로 Member와 Wallet을 Join
     * 페치조인으로 한번의 쿼리 생성
     *
     * @param email 조회할 회원의 이메일
     * @return 조회된 Wallet 객체
     */
    @Query("select w from Wallet w join fetch w.member m where m.email = :email")
    Optional<Wallet> findByMemberEmail(@Param("email") String email);
}