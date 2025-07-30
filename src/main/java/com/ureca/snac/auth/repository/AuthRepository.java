package com.ureca.snac.auth.repository;

import com.ureca.snac.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

}
