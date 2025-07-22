package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.member.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {
    Optional<Card> findByIdAndMember(Long cardId, Member member);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> findLockedById(Long cardId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> findLockedByIdAndMember(Long cardId, Member member);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Card> findLockedByMemberAndSellStatusAndCardCategory(Member member, SellStatus sellStatus, CardCategory cardCategory);
}
