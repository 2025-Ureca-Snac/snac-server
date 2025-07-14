package com.ureca.snac.money.repository;

import com.ureca.snac.money.entity.MoneyRecharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyRechargeRepository extends JpaRepository<MoneyRecharge, Long> {
    Optional<MoneyRecharge> findByPgOrderId(String pgOrderId);
    // 토스가 보내주는 주문번호를 통해 우리 시스템에 저장된 충전 요청 기록을 찾는다.
}
