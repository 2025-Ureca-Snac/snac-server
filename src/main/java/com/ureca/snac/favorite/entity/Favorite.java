package com.ureca.snac.favorite.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캡슐화
@Table(name = "favorite", uniqueConstraints = {
        @UniqueConstraint(
                name = "favorite_uk",
                columnNames = {"from_member_id", "to_member_id"} // 데이터 무결성 검증
        )
})
public class Favorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember; // 단골 등록하는 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember; // 단골 등록당하는 회원

    // 빌더 패턴과 private 생성자
    @Builder
    private Favorite(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
    }
}
