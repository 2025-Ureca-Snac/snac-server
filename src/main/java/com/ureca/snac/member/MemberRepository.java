package com.ureca.snac.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    Optional<Member> findEmailByPhone(String phone);

    boolean existsByEmail(String email);
}
