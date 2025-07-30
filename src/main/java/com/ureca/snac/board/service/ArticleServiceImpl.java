package com.ureca.snac.board.service;

import com.ureca.snac.board.entity.Article;
import com.ureca.snac.board.exception.ArticleNotFoundException;
import com.ureca.snac.board.repository.ArticleRepository;
import com.ureca.snac.board.service.response.ArticleResponse;
import com.ureca.snac.board.service.response.ListArticleResponse;
import com.ureca.snac.common.s3.S3Path;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
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
    public Long createArticle(String title, MultipartFile file, MultipartFile image, String username) {
        String s3KeyByFile = s3Uploader.upload(file, S3Path.ARTICLE_CONTENT);
        String s3KeyByImage = s3Uploader.upload(image, S3Path.ARTICLE_CONTENT);

        Member member = memberRepository
                .findByEmail(username)
                .orElseThrow(MemberNotFoundException::new);

        Article article = Article.builder()
                .member(member)
                .title(title)
                .articleUrl(s3KeyByFile)
                .imageUrl(s3KeyByImage)
                .build();

        Article savedArticle = articleRepository.save(article);

        return savedArticle.getId();
    }

    @Override
    public ArticleResponse getArticle(Long articleId) {
        return articleRepository
                .findById(articleId)
                .map(article -> ArticleResponse.from(
                        article,
                        s3Uploader.generatePresignedUrl(article.getArticleUrl()),
                        s3Uploader.generatePresignedUrl(article.getImageUrl()))
                )
                .orElseThrow(ArticleNotFoundException::new);
    }

    @Override
    public ListArticleResponse getArticles(Long lastArticleId, Integer size) {
        List<Article> articles = articleRepository.findArticlesByCursor(lastArticleId, size + 1);

        // 실제 응답에 넣을 리스트 (size개만 자르기)
        List<ArticleResponse> result = articles.stream()
                .limit(size)
                .map(article -> ArticleResponse.from(
                        article,
                        s3Uploader.generatePresignedUrl(article.getArticleUrl()),
                        s3Uploader.generatePresignedUrl(article.getImageUrl()))
                )
                .toList();

        // size+1개가 존재하면 다음 페이지 있음!
        boolean hasNext = articles.size() > size;

        return new ListArticleResponse(result, hasNext);
    }

    @Override
    @Transactional
    public Long updateArticle(Long articleId, String title, MultipartFile file, MultipartFile image, String username) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);

        Article article = articleRepository.findByMemberAndId(member, articleId).orElseThrow(ArticleNotFoundException::new);

        s3Uploader.delete(article.getArticleUrl());
        s3Uploader.delete(article.getImageUrl());

        String s3KeyByFile = s3Uploader.upload(file, S3Path.ARTICLE_CONTENT);
        String s3KeyByImage = s3Uploader.upload(image, S3Path.ARTICLE_CONTENT);

        article.update(s3KeyByFile, s3KeyByImage, title);

        return article.getId();
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId, String username) {
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        Article article = articleRepository.findByMemberAndId(member, articleId).orElseThrow(ArticleNotFoundException::new);

        s3Uploader.delete(article.getArticleUrl());
        s3Uploader.delete(article.getImageUrl());

        articleRepository.delete(article);
    }
}
