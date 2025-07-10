package com.ureca.snac.auth.repository;

import com.ureca.snac.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Member findByEmail(String email);

}
