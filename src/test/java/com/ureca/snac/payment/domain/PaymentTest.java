package com.ureca.snac.payment.domain;

import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.entity.PaymentStatus;
import com.ureca.snac.payment.exception.AlreadyUsedRechargeCannotCancelException;
import com.ureca.snac.payment.exception.PaymentAlreadyProcessedPaymentException;
import com.ureca.snac.payment.exception.PaymentAmountMismatchException;
import com.ureca.snac.payment.exception.PaymentOwnershipMismatchException;
import com.ureca.snac.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.ureca.snac.common.BaseCode.INVALID_INPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PaymentTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
    }

    // 팩토리 메소드
    @Test
    void 결제요청이_유효하면_보류_상태로_객체_생성() {
        // given
        Long validAmount = 10000L;

        // when
        Payment payment = Payment.prepare(member, validAmount);

        // then
        assertThat(payment).isNotNull();
        assertThat(payment.getMember()).isEqualTo(member);
        assertThat(payment.getAmount()).isEqualTo(validAmount);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getOrderId()).startsWith("snac_order_");
    }


    @Test
    void 결제요청_금액이_0원이면_예외_발생() {
        // given
        Long invalidAmount = 0L;

        // when then
        assertThatThrownBy(() -> Payment.prepare(member, invalidAmount))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("baseCode", INVALID_INPUT);
    }

    // 상태 변경 메소드
    @Test
    void 결제_성공시_완료_상태로_변경() {
        // given
        Payment payment = TestFixture.createPendingPayment(member);
        String paymentKey = "test_payment_key";
        String method = "카드";
        OffsetDateTime paidAt = OffsetDateTime.now();

        // when
        payment.complete(paymentKey, method, paidAt);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getMethod()).isEqualTo(method);
        assertThat(payment.getPaidAt()).isEqualTo(paidAt);
    }

    @Test
    void 결제_취소시_취소_상태로_변경() {
        // given
        Payment payment = TestFixture.createSuccessPayment(member,
                10000L, "카드", OffsetDateTime.now());
        String reason = "고객 변심";

        // when
        payment.cancel(reason);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getCancelReason()).isEqualTo(reason);
    }

    @Test
    void 예상된_실패시_취소_상태로_변경() {
        // given
        Payment payment = TestFixture.createPendingPayment(member);

        // when
        payment.reportFailureAsCancellation("INVALID_CARD_NUMBER", "잘못된 카드 번호");

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getFailureCode()).isEqualTo("INVALID_CARD_NUMBER");
        assertThat(payment.getFailureMessage()).isEqualTo("잘못된 카드 번호");
    }

    @Test
    void 예상치못한_실패시_실패_상태로_변경() {
        // given
        Payment payment = TestFixture.createPendingPayment(member);

        // when
        payment.recordFailure("INTERNAL_SERVER_ERROR", "내부 서버 오류");

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAIL);
        assertThat(payment.getFailureCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(payment.getFailureMessage()).isEqualTo("내부 서버 오류");
    }

    @Test
    void 이미_처리된_결제_실패_변경_예외_발생() {
        // given
        Payment payment = TestFixture.createSuccessPayment(member,
                10000L, "카드", OffsetDateTime.now());
        // when then
        assertThatThrownBy(() -> payment.recordFailure("FAIL_CODE", "FAIL_MESSAGE"))
                .isInstanceOf(PaymentAlreadyProcessedPaymentException.class);
    }

    // 유효성 검증 메소드
    @Test
    void 결제_승인_검증시_모든_조건이_유효하면_예외_발생_안함() {
        // given
        Payment payment = TestFixture.createPendingPayment(member, 10000L);

        // when then
        assertDoesNotThrow(() -> payment.validateForConfirmation(member, 10000L));
    }

    @Test
    void 결제_승인_검증시_사람이_다르면_예외_발생() {
        // given
        Member owner = TestFixture.createTestMember(1L, "owner@test.com");
        Member otherMember = TestFixture.createTestMember(2L, "other@test.com");
        Payment payment = TestFixture.createPendingPayment(owner, 10000L);

        // when then
        assertThatThrownBy(() -> payment.validateForConfirmation(otherMember, 10000L))
                .isInstanceOf(PaymentOwnershipMismatchException.class);
    }

    @Test
    void 결제_승인_검증시_금액이_다르면_예외_발생() {
        // given
        Member owner = TestFixture.createTestMember();
        Payment payment = TestFixture.createPendingPayment(owner, 10000L);
        Long wrongAmount = 5000L;

        // when then
        assertThatThrownBy(() -> payment.validateForConfirmation(owner,
                wrongAmount))
                .isInstanceOf(PaymentAmountMismatchException.class);
    }

    @Test
    void 결제_승인_검증시_이미_처리된_결제면_예외_발생() {
        // given
        Payment payment = TestFixture.createSuccessPayment(member, 10000L, "카드", OffsetDateTime.now());

        // when then
        assertThatThrownBy(() -> payment.validateForConfirmation(member, 10000L))
                .isInstanceOf(PaymentAlreadyProcessedPaymentException.class);
    }

    @Test
    void 결제_취소_검증시_모든_조건이_유효하면_예외_발생_안함() {
        // given
        Payment payment = TestFixture.createSuccessPayment(member, 10000L, "카드", OffsetDateTime.now().minusDays(10));
        Long balance = 15000L;

        // when then
        assertDoesNotThrow(() -> payment.validateForCancellation(member, balance));
    }

    @Test
    void 결제_취소_검증시_잔액_부족하면_예외_발생() {
        // given
        Payment payment = TestFixture.createSuccessPayment(member, 10000L,
                "카드", OffsetDateTime.now());
        Long insufficientBalance = 5000L;

        // when then
        assertThatThrownBy(() -> payment.validateForCancellation(member,
                insufficientBalance))
                .isInstanceOf(AlreadyUsedRechargeCannotCancelException.class);
    }
}
