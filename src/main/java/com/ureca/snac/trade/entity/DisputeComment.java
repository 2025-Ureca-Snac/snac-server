package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "dispute_comment")
public class DisputeComment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_id", nullable = false)
    private Dispute dispute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private AuthorType author; // USER or ADMIN

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 메시지 본문

    @Builder
    public DisputeComment(Dispute dispute, Member writer, AuthorType author, String content) {
        this.dispute = dispute;
        this.writer = writer;
        this.author = author;
        this.content = content;
    }
}