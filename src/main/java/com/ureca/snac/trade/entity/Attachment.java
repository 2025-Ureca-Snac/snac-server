package com.ureca.snac.trade.entity;

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
@Table(name = "attachment")
public class Attachment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member uploader; // 데이터 판매하는 사람

    @Column(name = "s3_key", nullable = false)
    private String s3Key; // S3 에 저장된 고유키 ( 예: trade/attachments/2025/07/uuid_image.png)

    @Builder
    private Attachment(Trade trade, Member uploader, String s3Key) {
        this.trade = trade;
        this.uploader = uploader;
        this.s3Key = s3Key;
    }
}
