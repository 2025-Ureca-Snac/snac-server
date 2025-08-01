package com.ureca.snac.board.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @Column(name = "artice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "article_url", nullable = false)
    private String articleUrl;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "title")
    private String title;

    @Builder
    private Article(Member member, String articleUrl, String imageUrl, String title) {
        this.member = member;
        this.articleUrl = articleUrl;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public void update(String articleUrl, String imageUrl, String title) {
        this.articleUrl = articleUrl;
        this.imageUrl = imageUrl;
        this.title = title;
    }
}
