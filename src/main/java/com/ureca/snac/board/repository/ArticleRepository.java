package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
