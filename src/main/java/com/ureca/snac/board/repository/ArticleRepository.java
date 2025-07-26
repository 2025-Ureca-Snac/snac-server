package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Article;
import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByMemberAndId(Member member, Long articleId);
}
