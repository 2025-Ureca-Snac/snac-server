package com.ureca.snac.money.repository;

import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MoneyRechargeRepository extends JpaRepository<MoneyRecharge, Long> {

    /**
     * Payment 객체 MoneyRecharge 조회
     *
     * @param payment Payment 객체
     * @return MoneyRecharge 객체
     */
    Optional<MoneyRecharge> findByPayment(Payment payment);

    /**
     * 비관적 락 사용 결제 취소랑 고려했을 때 데이터 정합성 보장
     *
     * @param rechargeId 락걸어 충전 내역 ID
     * @return 락 걸린 상태 MoneyRecharge 객체
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mr from MoneyRecharge mr where mr.id= :rechargeId")
    Optional<MoneyRecharge> findByIdWithLock(@Param("rechargeId") Long rechargeId);

}
