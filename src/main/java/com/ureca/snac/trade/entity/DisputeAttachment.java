package com.ureca.snac.trade.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "dispute_attachment")
public class DisputeAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_id", nullable = false)
    private Dispute dispute;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @Builder
    public DisputeAttachment(Dispute dispute, String s3Key) {
        this.dispute = dispute;
        this.s3Key = s3Key;
    }
}