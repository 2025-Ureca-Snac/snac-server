package com.ureca.snac.trade.repository;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByMember(Member member);
}