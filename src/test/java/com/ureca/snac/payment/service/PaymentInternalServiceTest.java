package com.ureca.snac.payment.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.entity.PaymentStatus;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.support.TestFixture;
import com.ureca.snac.wallet.exception.InsufficientBalanceException;
import com.ureca.snac.wallet.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentInternalServiceTest {

    @InjectMocks
    private PaymentInternalService paymentInternalService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AssetHistoryEventPublisher assetHistoryEventPublisher;

    @Mock
    private AssetChangedEventFactory assetChangedEventFactory;

    private Member member;
    private Payment payment;
    private PaymentCancelResponse cancelResponse;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
        payment = TestFixture.createSuccessPayment
                (member, 10000L, "카드", OffsetDateTime.now());
        cancelResponse = new PaymentCancelResponse(
                "test_payment_key", 10000L,
                OffsetDateTime.now(), "테스트 취소 사유");
    }

    @Test
    void 결제_취소_내부_처리_성공() {
        // given
        Long balanceAfterWithdraw = 5000L;
        AssetChangedEvent dummyEvent = TestFixture.createDummyEvent();

        when(walletService.withdrawMoney(anyLong(), anyLong())).
                thenReturn(balanceAfterWithdraw);

        when(assetChangedEventFactory.createForRechargeCancel(any(), any(), any(), any()))
                .thenReturn(dummyEvent);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        // when
        paymentInternalService.processCancellationInDB(payment, member, cancelResponse);

        // then
        // Payment 엔티티 상태
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getCancelReason()).isEqualTo("테스트 취소 사유");

        // 외부 객체 호출 여부
        verify(walletService).withdrawMoney(member.getId(), payment.getAmount());
        verify(assetChangedEventFactory).createForRechargeCancel(
                member.getId(),
                payment.getId(),
                payment.getAmount(),
                balanceAfterWithdraw
        );

        verify(assetHistoryEventPublisher).publish(dummyEvent);
        verify(paymentRepository).save(payment);
    }

    @Test
    void 머니_출금_실패시_예외_발생_이벤트_발행_안함() {
        // given
        when(walletService.withdrawMoney(anyLong(), anyLong()))
                .thenThrow(new InsufficientBalanceException());

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Assertions.assertThrows(InsufficientBalanceException.class,
                () -> paymentInternalService.processCancellationInDB(payment, member, cancelResponse)
        );

        // then
        verify(assetHistoryEventPublisher, never()).publish(any(AssetChangedEvent.class));
    }
}