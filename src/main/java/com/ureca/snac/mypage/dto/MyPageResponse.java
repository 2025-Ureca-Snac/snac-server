package com.ureca.snac.mypage.dto;

import com.ureca.snac.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class MyPageResponse {
    private String name;
    private String phone;
    private LocalDate birthDate;
    private int score;
    private LocalDateTime nicknameUpdatedAt;
    private boolean isNaverConnected;
    private boolean isGoogleConnected;
    private boolean isKakaoConnected;
    private Long favoriteCount;

    public static MyPageResponse from(Member member) {
        return MyPageResponse.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .birthDate(member.getBirthDate())
                .score(member.getRatingScore())
                .nicknameUpdatedAt(member.getNicknameUpdatedAt())
                .isNaverConnected(member.getNaverId() != null && !member.getNaverId().isEmpty())
                .isGoogleConnected(member.getGoogleId() != null && !member.getGoogleId().isEmpty())
                .isKakaoConnected(member.getKakaoId() != null && !member.getKakaoId().isEmpty())
                .build();
    }
}