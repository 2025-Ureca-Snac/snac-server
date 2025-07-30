package com.ureca.snac.board.service.response;

import com.ureca.snac.board.entity.Article;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleResponse {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String articleUrl;
    private String imageUrl;
    private String title;

    public static ArticleResponse from(Article article, String generateArticleKey, String generateImageKey) {
        return ArticleResponse.builder()
                .id(article.getId())
                .email(article.getMember().getEmail())
                .name(article.getMember().getName())
                .nickname(article.getMember().getNickname())
                .articleUrl(generateArticleKey)
                .imageUrl(generateImageKey)
                .title(article.getTitle())
                .build();
    }

}
