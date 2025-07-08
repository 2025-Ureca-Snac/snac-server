package com.ureca.snac.finance.repository;

import com.ureca.snac.finance.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
