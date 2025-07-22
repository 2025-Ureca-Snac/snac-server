package com.ureca.snac.member;

import com.ureca.snac.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "nicknameUpdatedAt")
    private LocalDateTime nicknameUpdatedAt;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "rating_score", nullable = false)
    private Integer ratingScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "activated", nullable = false)
    private Activated activated;

    private String naverId;

    private String googleId;


    private String kakaoId;

    @Builder
    private Member(String email, String password, String name, String nickname, LocalDateTime nicknameUpdatedAt, String phone, LocalDate birthDate,
                   Integer ratingScore, Role role, Activated activated, String naverId, String googleId, String kakaoId) {

        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.nicknameUpdatedAt = nicknameUpdatedAt;
        this.phone = phone;
        this.birthDate = birthDate;
        this.ratingScore = ratingScore;
        this.role = role;
        this.activated = activated;
        this.naverId = naverId;
        this.googleId = googleId;
        this.kakaoId = kakaoId;
    }

    // 소셜 아이디 업데이트 용
    public void updateSocialId(String provider, String providerId) {
        switch (provider) {
            case "naver":
                this.naverId = providerId;
                break;
            case "google":
                this.googleId = providerId;
                break;
            case "kakao":
                this.kakaoId = providerId;
                break;
        }
    }

    public void changePasswordTo(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changePhoneTo(String newPhone) {
        this.phone = newPhone;
    }

    public void changeNicknameTo(String newNickname) {
        this.nickname = newNickname;
        this.nicknameUpdatedAt = LocalDateTime.now();
    }
}