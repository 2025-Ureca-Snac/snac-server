package com.ureca.snac.money.domain;

import com.ureca.snac.member.entity.Member;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.exception.InvalidPaymentForRechargeException;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyRechargeTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
    }

    // 팩토리 메소드 테스트
    @Test
    void Payment가_성공_상태_MoneyRecharge_생성_성공() {
        // given
        Payment successPayment = TestFixture.
                createSuccessPayment(member, 10000L, "카드", OffsetDateTime.now());

        // when
        // MoneyRecharge는 Payment 객체 받아서 생성한다.
        MoneyRecharge moneyRecharge = MoneyRecharge.create(successPayment);

        // then
        assertThat(moneyRecharge).isNotNull();
        assertThat(moneyRecharge.getMember()).isEqualTo(successPayment.getMember());
        assertThat(moneyRecharge.getPaidAmountWon()).isEqualTo(successPayment.getAmount());
        assertThat(moneyRecharge.getPayment()).isEqualTo(successPayment);
    }

    @Test
    void Payment가_성공_아니면_예외발생() {
        // given
        Payment pendingPayment = TestFixture.createPendingPayment(member); // PENDING 상태

        // when then
        assertThatThrownBy(() -> MoneyRecharge.create(pendingPayment))
                .isInstanceOf(InvalidPaymentForRechargeException.class);
    }
}