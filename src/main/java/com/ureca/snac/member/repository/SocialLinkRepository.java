package com.ureca.snac.member.repository;

import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
    Optional<SocialLink> findByProviderAndProviderId(SocialProvider provider, String providerId);
    Optional<SocialLink> findByMemberAndProvider(Member member, SocialProvider provider);
}
