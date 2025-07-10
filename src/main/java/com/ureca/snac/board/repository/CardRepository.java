package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {
    Optional<Card> findByIdAndMember(Long cardId, Member member);
}
