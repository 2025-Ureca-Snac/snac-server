package com.ureca.snac.auth.repository;

import com.ureca.snac.member.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNaverId(String naverId);
    Optional<Member> findByGoogleId(String googleId);
    Optional<Member> findByKakaoId(String kakaoId);

    default Optional<Member> findBySocialProviderId(SocialProvider provider, String providerId) {
        return switch (provider) {
            case NAVER  -> findByNaverId(providerId);
            case GOOGLE -> findByGoogleId(providerId);
            case KAKAO  -> findByKakaoId(providerId);
        };
    }
}
