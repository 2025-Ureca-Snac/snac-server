package com.ureca.snac.money.service;

import com.ureca.snac.common.exception.InternalServerException;
import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentRecoveryService;
import com.ureca.snac.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ureca.snac.common.BaseCode.PAYMENT_INTERNAL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyServiceImplTest {

    // 테스트할 대상은 구현체
    @InjectMocks
    private MoneyServiceImpl moneyServiceImpl;

    // 가짜 주입
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayAdapter paymentGatewayAdapter;

    @Mock
    private MoneyDepositor moneyDepositor;

    @Mock
    PaymentRecoveryService paymentRecoveryService;

    private Member member;
    private Payment payment;
    private TossConfirmResponse tossConfirmResponse;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
        payment = TestFixture.createPendingPayment(member);
        tossConfirmResponse = TestFixture.createTossConfirmResponse();
    }

    @Test
    void 머니_충전_성공_시나리오() {
        // given
        when(memberRepository.findByEmail(anyString())).
                thenReturn(Optional.of(member));

        when(paymentRepository.findByOrderIdWithMember(anyString()))
                .thenReturn(Optional.of(payment));

        when(paymentGatewayAdapter.confirmPayment(anyString(), anyString(), anyLong()))
                .thenReturn(tossConfirmResponse);

        // when
        moneyServiceImpl.processRechargeSuccess(
                "test_key_id", "test_order_id", 10000L, "test@test.com");

        // then
        verify(paymentRepository).findByOrderIdWithMember("test_order_id");
        verify(paymentGatewayAdapter).confirmPayment("test_key_id", "test_order_id", 10000L);
        verify(moneyDepositor).deposit(payment, member, tossConfirmResponse);
        // 호출 X
        verify(paymentRecoveryService, never()).processInternalFailure(any(), any());
    }

    @Test
    void 결제성공_후_내부_DB에서_처리실패_시_복구_로직_호출_및_예외() {
        // given
        RuntimeException dbException = new RuntimeException("DB 연결 실패");

        when(memberRepository.findByEmail(anyString())).
                thenReturn(Optional.of(member));

        when((paymentRepository.findByOrderIdWithMember(anyString()))).
                thenReturn(Optional.of(payment));

        when(paymentGatewayAdapter.confirmPayment(anyString(), anyString(), anyLong()))
                .thenReturn(tossConfirmResponse);

        // deposit 호출 예외발생위해
        doThrow(dbException).when(moneyDepositor)
                .deposit(any(Payment.class), any(Member.class), any(TossConfirmResponse.class));

        // InternalServerException 이 던져지는지 검증
        // when then
        InternalServerException thrownException = assertThrows(
                InternalServerException.class,
                () -> moneyServiceImpl.processRechargeSuccess("test_key_id",
                        "test_order_id", 10000L, "test@tset.com"
                )
        );

        // 2. 던져진 예외가 우리가 의도한건지 확인
        assertThat(thrownException.getBaseCode()).isEqualTo(PAYMENT_INTERNAL_ERROR);

        verify(paymentRecoveryService, times(1))
                .processInternalFailure(payment, dbException);
    }
}