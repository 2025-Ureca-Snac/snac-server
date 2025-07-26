package com.ureca.snac.board.service;

import com.ureca.snac.board.controller.request.CreateArticleRequest;
import com.ureca.snac.board.controller.request.UpdateArticleRequest;
import com.ureca.snac.board.entity.Article;
import com.ureca.snac.board.repository.ArticleRepository;
import com.ureca.snac.board.service.response.ArticleResponse;
import com.ureca.snac.common.s3.S3Path;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public Long createArticle(CreateArticleRequest request, MultipartFile file, String username) {
        String s3Key = s3Uploader.upload(file, S3Path.ARTICLE_CONTENT);

        Member member = memberRepository
                .findByEmail(username)
                .orElseThrow(MemberNotFoundException::new);

        Article article = Article.builder()
                .member(member)
                .title(request.getTitle())
                .articleUrl(s3Key)
                .build();

        Article savedArticle = articleRepository.save(article);

        return savedArticle.getId();
    }

    @Override
    public ArticleResponse getArticle(Long articleId) {
        return null;
    }

    @Override
    public List<ArticleResponse> getArticles() {
        return List.of();
    }

    @Override
    public ArticleResponse updateArticle(Long articleId, UpdateArticleRequest request, String username) {
        return null;
    }

    @Override
    public void deleteArticle(Long articleId, String username) {

    }
}
