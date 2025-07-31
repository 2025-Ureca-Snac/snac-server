package com.ureca.snac.finance.repository;

import com.ureca.snac.finance.entity.Account;
import com.ureca.snac.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByMember(Member member);

    Optional<Account> findByMember(Member member);
}
