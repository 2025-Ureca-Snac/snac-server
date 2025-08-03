package com.ureca.snac.payment.service;

import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.entity.PaymentStatus;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRecoveryServiceImplTest {

    @InjectMocks
    private PaymentRecoveryServiceImpl paymentRecoveryService;

    @Mock
    private PaymentRepository paymentRepository;

    private Payment payment;
    private Exception exception;

    @BeforeEach
    void setUp() {
        payment = TestFixture.createPendingPayment(TestFixture.createTestMember());
        exception = new RuntimeException("최초 에러");
    }

    @Test
    void 재난_복구_성공_결제_상태_실패() {
        // given
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when 호출
        paymentRecoveryService.processInternalFailure(payment, exception);

        // then
        // 1 디비 호출
        verify(paymentRepository).findById(payment.getId());
        verify(paymentRepository).save(any(Payment.class));

        // 객체 상태 변경 검증
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAIL);
        assertThat(payment.getFailureCode()).isEqualTo("INTERNAL_DB_ERROR");
        assertThat(payment.getFailureMessage()).isEqualTo("최초 에러");
    }

    @Test
    void 재난_복구_실패_예외_발생_안함() {
        // given
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("데이터 베이스 연결 실패"));


        // when then
        // 재난 복구 호출시 예외처리하고 추가 예외안던지는지
        assertDoesNotThrow(() -> {
            paymentRecoveryService.processInternalFailure(payment, exception);
        });

        verify(paymentRepository).save(any(Payment.class));
    }
}