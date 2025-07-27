package com.ureca.snac.board.repository;

import com.ureca.snac.board.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findArticlesByCursor(Long lastArticleId, Integer size);
}
