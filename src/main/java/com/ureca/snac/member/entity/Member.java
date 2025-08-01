package com.ureca.snac.member.entity;

import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Activated;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.exception.NicknameChangeTooEarlyException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
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


    @Column(name = "suspend_until")
    private LocalDateTime suspendUntil; // 임시 정지 만료일

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialLink> socialLinks = new HashSet<>();


    @Builder
    private Member(String email, String password, String name, String nickname, LocalDateTime nicknameUpdatedAt, String phone, LocalDate birthDate,
                   Integer ratingScore, Role role, Activated activated) {

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
    }

    public void addSocialLink(SocialProvider provider, String providerId) {
        SocialLink socialLink = SocialLink.builder()
                .member(this)
                .provider(provider)
                .providerId(providerId)
                .build();
        this.socialLinks.add(socialLink);
    }

    public void removeSocialLink(SocialProvider provider) {
        this.socialLinks.removeIf(link -> link.getProvider() == provider);
    }

    public Optional<SocialLink> getSocialLink(SocialProvider provider) {
        return this.socialLinks.stream()
                .filter(link -> link.getProvider() == provider)
                .findFirst();
    }

    public boolean isConnected(SocialProvider provider) {
        return this.socialLinks.stream().anyMatch(link -> link.getProvider() == provider);
    }

    public void changePasswordTo(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changePhoneTo(String newPhone) {
        this.phone = newPhone;
    }

    public void changeNicknameTo(String newNickname) {
        if (nicknameUpdatedAt != null && nicknameUpdatedAt.isAfter(LocalDateTime.now().minusMinutes(3)/*minusDays(1)*/)) {
            throw new NicknameChangeTooEarlyException();
        }
        this.nickname = newNickname;
        this.nicknameUpdatedAt = LocalDateTime.now();
    }

    // 임시 정지
    public void suspendUntil(LocalDateTime until) {
        this.activated = Activated.TEMP_SUSPEND;
        this.suspendUntil = until;
    }

    // 영구 정지
    public void permanentBan() {
        this.activated = Activated.PERMANENT_BAN;
        this.suspendUntil = null;
    }

    // 정지 해제
    public void activate() {
        this.activated = Activated.NORMAL;
        this.suspendUntil = null;
    }
}