package com.ureca.snac.member.repository;

import com.ureca.snac.member.Activated;
import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);

    // TEMP_SUSPEND 상태이면서 suspendUntil < now 인 회원 목록 조회
    List<Member> findByActivatedAndSuspendUntilBefore(Activated activated, LocalDateTime before);

    @Query("select m.email from Member m where m.phone = :phone")
    Optional<String> findEmailByPhone(String phone);

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);

}