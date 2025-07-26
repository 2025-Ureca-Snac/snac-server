package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateArticleRequest;
import com.ureca.snac.board.controller.request.UpdateArticleRequest;
import com.ureca.snac.board.service.response.ArticleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {
    Long createArticle(CreateArticleRequest request, MultipartFile file, String username);

    ArticleResponse getArticle(Long articleId);

    List<ArticleResponse> getArticles();

    Long updateArticle(Long articleId, UpdateArticleRequest request, MultipartFile file, String username);

    void deleteArticle(Long articleId, String username);
}
