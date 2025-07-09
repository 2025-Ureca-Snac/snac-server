package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
