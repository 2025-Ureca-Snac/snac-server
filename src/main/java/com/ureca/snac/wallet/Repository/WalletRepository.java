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
}