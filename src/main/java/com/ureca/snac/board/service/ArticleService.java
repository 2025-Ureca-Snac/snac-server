package com.ureca.snac.board.service;

import com.ureca.snac.board.service.response.ArticleResponse;
import com.ureca.snac.board.service.response.ListArticleResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    Long createArticle(String title, MultipartFile file, MultipartFile image, String username);

    ArticleResponse getArticle(Long articleId);

    ListArticleResponse getArticles(Long lastArticleId, Integer size);

    Long updateArticle(Long articleId, String title, MultipartFile file, MultipartFile image, String username);

    void deleteArticle(Long articleId, String username);
}
