package com.ureca.snac.money.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.entity.PaymentStatus;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.support.TestFixture;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import com.ureca.snac.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyDepositorTest {

    @InjectMocks
    private MoneyDepositor moneyDepositor;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MoneyRechargeRepository moneyRechargeRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AssetHistoryEventPublisher assetHistoryEventPublisher;

    @Mock
    private AssetChangedEventFactory assetChangedEventFactory;

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
    void 머니_입금_처리시_정상적_상호작용() {
        // given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        when(moneyRechargeRepository.save(any(MoneyRecharge.class))).thenReturn(null);
        // 지갑 서비스 입금 요청
        when(walletService.depositMoney(anyLong(), anyLong())).
                thenReturn(20000L);
        // 이벤트 생성
        when(assetChangedEventFactory.createForRechargeEvent(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(TestFixture.createDummyEvent());

        // when 테스트 시작
        moneyDepositor.deposit(payment, member, tossConfirmResponse);

        // then
        // Payment 저장
        verify(paymentRepository).save(payment);

        // MoneyRecharge 저장
        verify(moneyRechargeRepository).save(any(MoneyRecharge.class));

        // wallet 증가
        verify(walletService).depositMoney(member.getId(), payment.getAmount());

        // Asset 이벤트
        verify(assetChangedEventFactory).createForRechargeEvent(anyLong(), anyLong(), anyLong(), anyLong());
        verify(assetHistoryEventPublisher).publish(any(AssetChangedEvent.class));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void 지갑_입금_실패시_충전기록_저장_안됨() {
        // given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        when(moneyRechargeRepository.save(any(MoneyRecharge.class))).thenReturn(null);
        when(walletService.depositMoney(member.getId(), 10000L))
                .thenThrow(new WalletNotFoundException());

        // when then
        assertThatThrownBy(() -> moneyDepositor.
                deposit(payment, member, tossConfirmResponse))
                .isInstanceOf(WalletNotFoundException.class);

        // then 트랜잭션 롤백
        verify(assetHistoryEventPublisher, never()).publish(any(AssetChangedEvent.class));
    }
}