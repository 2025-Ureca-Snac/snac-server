package com.ureca.snac.favorite.dto;

import com.ureca.snac.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단골 목록의 개별 회원 정보 DTO")
public record FavoriteMemberDto(
        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "회원 닉네임", example = "유레카")
        String nickname
) {
    /**
     * Member 엔티티 FavoriteMemberDto 변환하는 정적 팩토리 메소드
     * API 응답 필요 선별
     *
     * @param member 변환 엔티티
     * @return dto
     */
    public static FavoriteMemberDto from(Member member) {
        return new FavoriteMemberDto(
                member.getId(),
                member.getNickname()
        );
    }
}
