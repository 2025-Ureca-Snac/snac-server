package com.ureca.snac.board.controller;

import com.ureca.snac.board.controller.request.CreateArticleRequest;
import com.ureca.snac.board.controller.request.UpdateArticleRequest;
import com.ureca.snac.board.service.ArticleService;
import com.ureca.snac.board.service.response.CreateArticleResponse;
import com.ureca.snac.board.service.response.UpdateArticleResponse;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
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
    public ResponseEntity<ApiResponse<CreateArticleResponse>> addArticle(@ModelAttribute CreateArticleRequest createArticleRequest,
                                                                         @RequestPart MultipartFile file,
                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long articleId = articleService.createArticle(createArticleRequest, file, userDetails.getUsername());

        return ResponseEntity.status(ARTICLE_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.of(ARTICLE_CREATE_SUCCESS, new CreateArticleResponse(articleId)));
    }

    @PutMapping(value = "/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> editArticle(@PathVariable("articleId") Long articleId,
                                                      @ModelAttribute UpdateArticleRequest updateArticleRequest,
                                                      @RequestPart MultipartFile file,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        Long updateArticleId = articleService.updateArticle(articleId, updateArticleRequest, file, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of(ARTICLE_UPDATE_SUCCESS, new UpdateArticleResponse(updateArticleId)));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<?>> removeArticle(@PathVariable("articleId") Long articleId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(articleId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(ARTICLE_DELETE_SUCCESS));
    }

}
