package com.ureca.snac.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    // TEMP_SUSPEND 상태이면서 suspendUntil < now 인 회원 목록 조회
    List<Member> findByActivatedAndSuspendUntilBefore(Activated activated, LocalDateTime before);
}