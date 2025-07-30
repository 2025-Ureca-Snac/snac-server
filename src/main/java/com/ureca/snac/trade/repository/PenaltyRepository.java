package com.ureca.snac.trade.repository;

import com.ureca.snac.member.entity.Member;
import com.ureca.snac.trade.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    Optional<Penalty> findTopByMemberOrderByCreatedAtDesc(Member member);
    long countByMember(Member member);
}