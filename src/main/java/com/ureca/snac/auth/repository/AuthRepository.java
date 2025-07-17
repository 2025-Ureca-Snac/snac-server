package com.ureca.snac.auth.repository;

import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNaverId(String naverId);
    Optional<Member> findByGoogleId(String googleId);
    Optional<Member> findByKakaoId(String kakaoId);
}
