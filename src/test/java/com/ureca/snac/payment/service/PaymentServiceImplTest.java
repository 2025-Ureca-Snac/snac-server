package com.ureca.snac.payment.service;

import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentNotCancellableException;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.support.TestFixture;
import com.ureca.snac.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayAdapter paymentGatewayAdapter;

    @Mock
    private PaymentInternalService paymentInternalService;

    @Mock
    private WalletService walletService;

    private Member member;
    private Payment payment;
    private String paymentKey;
    private String reason;
    private String email;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember(1L, "test@test.com");
        payment = TestFixture.createSuccessPayment(
                member, 10000L, "카드", OffsetDateTime.now());
        paymentKey = "test_payment_key";
        reason = "테스트 취소";
        email = "test@test.com";
    }

    @Test
    void 결제_취소_성공() {
        // given
        PaymentCancelResponse dummyCancelResponse = new PaymentCancelResponse(
                "key", 10000L, OffsetDateTime.now(), "reason"
        );

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        when(paymentRepository.findByPaymentKeyWithMember(paymentKey))
                .thenReturn(Optional.of(payment));

        when(walletService.getMoneyBalance(member.getId()))
                .thenReturn(20000L);

        when(paymentGatewayAdapter.cancelPayment(paymentKey, reason))
                .thenReturn(dummyCancelResponse);

        // when
        PaymentCancelResponse response = paymentService.cancelPayment(paymentKey, reason, email);

        // then
        assertThat(response).isEqualTo(dummyCancelResponse);

        verify(paymentGatewayAdapter).cancelPayment(paymentKey, reason);
        verify(paymentInternalService).processCancellationInDB(payment, member, dummyCancelResponse);
    }

    @Test
    void 결제_취소시_사용자를_찾지_못하면_예외_발생() {
        // given
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        assertThrows(MemberNotFoundException.class, () -> {
            paymentService.cancelPayment(paymentKey, reason, email
            );
        });
        
        // then
        verify(paymentRepository, never()).findByPaymentKeyWithMember(anyString());
        verify(paymentGatewayAdapter, never()).cancelPayment(anyString(), anyString());
    }

    @Test
    void 결제_취소시_결제내역을_찾지못하면_예외_발생() {
        // given
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        when(paymentRepository.findByPaymentKeyWithMember(paymentKey))
                .thenReturn(Optional.empty());

        // when
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.cancelPayment(paymentKey, reason, email);
        });

        // then
        verify(paymentGatewayAdapter, never()).cancelPayment(anyString(), anyString());
    }

    @Test
    void 이미_취소된_결제건_검증_실패시_예외_발생() {
        // given 실패 상태
        Payment alreadyCanceledPayment = TestFixture.createPendingPayment(member);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        when(paymentRepository.findByPaymentKeyWithMember(paymentKey)).thenReturn(
                Optional.of(alreadyCanceledPayment));

        when(walletService.getMoneyBalance(member.getId()))
                .thenReturn(20000L);

        // when
        assertThrows(PaymentNotCancellableException.class, () -> {
            paymentService.cancelPayment(paymentKey, reason, email);
        });

        // then
        verify(paymentGatewayAdapter, never()).cancelPayment(anyString(), anyString());
        verify(paymentInternalService, never()).processCancellationInDB(any(), any(), any());
    }
}