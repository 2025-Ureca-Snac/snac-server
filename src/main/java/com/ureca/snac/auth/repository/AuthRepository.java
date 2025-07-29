package com.ureca.snac.auth.repository;

import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    Member findByNaverId(String naverId);
    Member findByGoogleId(String googleId);
    Member findByKakaoId(String kakaoId);
}
