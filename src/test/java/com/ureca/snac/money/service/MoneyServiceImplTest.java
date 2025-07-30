package com.ureca.snac.money.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.InternalServerException;
import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentRecoveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith((MockitoExtension.class))
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

    // 가짜 객체
    @Mock
    private Member mockMember;

    @Mock
    private Payment mockPayment;

    @Mock
    private TossConfirmResponse mockResponse;

    @Test
    void 결제성공_후_내부_DB에서_처리실패_시_복구_로직_호출_및_예외() {

        // 1. 테스트에 필요한 예외 객체
        RuntimeException dbException = new RuntimeException("DB 연결 실패");

        when(memberRepository.findByEmail(anyString())).
                thenReturn(Optional.of(mockMember));

        when((paymentRepository.findByOrderIdWithMember(anyString()))).
                thenReturn(Optional.of(mockPayment));

        when(paymentGatewayAdapter.confirmPayment(anyString(), anyString(), anyLong()))
                .thenReturn(mockResponse);

        // deposit 호출 예외발생위해
        doThrow(dbException).when(moneyDepositor)
                .deposit(any(Payment.class), any(Member.class), any(TossConfirmResponse.class));

        // InternalServerException 이 던져지는지 검증
        InternalServerException thrownException = assertThrows(
                InternalServerException.class,
                () -> moneyServiceImpl.processRechargeSuccess("test_key_id",
                        "test_order_id",
                        10L,
                        "test@tset.com"
                )
        );

        // 2. 던져진 예외가 우리가 의도한건지 확인
        assertEquals(BaseCode.PAYMENT_INTERNAL_ERROR, thrownException.getBaseCode());

        // 어떤 Payment 객체와 어떤 예외 객체가 전달되었는지 확인
        verify(paymentRecoveryService, times(1))
                .processInternalFailure(mockPayment, dbException);
    }
}