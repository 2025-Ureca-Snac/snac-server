package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.member.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {
    Optional<Card> findByIdAndMember(Long cardId, Member member);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> findLockedById(Long cardId);
}
