package com.ureca.snac.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.snac.board.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.snac.board.entity.QArticle.article;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Article> findArticlesByCursor(Long lastArticleId, Integer size) {
        return query
                .selectFrom(article)
                .where(lastArticleId != null ? article.id.lt(lastArticleId) : null)
                .orderBy(article.id.desc())
                .limit(size)
                .fetch();
    }
}
