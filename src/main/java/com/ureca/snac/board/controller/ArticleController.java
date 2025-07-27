package com.ureca.snac.board.controller;

import com.ureca.snac.board.service.ArticleService;
import com.ureca.snac.board.service.response.ArticleResponse;
import com.ureca.snac.board.service.response.CreateArticleResponse;
import com.ureca.snac.board.service.response.ListArticleResponse;
import com.ureca.snac.board.service.response.UpdateArticleResponse;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ureca.snac.common.BaseCode.*;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController implements ArticleControllerSwagger {

    private final ArticleService articleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateArticleResponse>> addArticle(@RequestParam String title,
                                                                         @RequestPart MultipartFile file,
                                                                         @RequestPart MultipartFile image,
                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long articleId = articleService.createArticle(title, file, image, userDetails.getUsername());

        return ResponseEntity.status(ARTICLE_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.of(ARTICLE_CREATE_SUCCESS, new CreateArticleResponse(articleId)));
    }

    @PutMapping(value = "/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> editArticle(@PathVariable("articleId") Long articleId,
                                                      @RequestParam String title,
                                                      @RequestPart MultipartFile file,
                                                      @RequestPart MultipartFile image,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        Long updateArticleId = articleService.updateArticle(articleId, title, file, image, userDetails.getUsername());

        return ResponseEntity
                .ok(ApiResponse.of(ARTICLE_UPDATE_SUCCESS, new UpdateArticleResponse(updateArticleId)));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<?>> removeArticle(@PathVariable("articleId") Long articleId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(articleId, userDetails.getUsername());

        return ResponseEntity
                .ok(ApiResponse.ok(ARTICLE_DELETE_SUCCESS));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(@PathVariable("articleId") Long articleId) {
        return ResponseEntity
                .ok(ApiResponse.of(ARTICLE_READ_SUCCESS, articleService.getArticle(articleId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ListArticleResponse>> getArticles(@RequestParam(required = false) Long lastArticleId,
                                                                        @RequestParam(defaultValue = "9") Integer size){

        return ResponseEntity
                .ok(ApiResponse.of(ACCOUNT_LIST_SUCCESS, articleService.getArticles(lastArticleId, size)));
    }
}
