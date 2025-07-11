package com.ureca.snac.money.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.money.dto.request.MoneyRechargeRequest;
import com.ureca.snac.money.dto.response.MoneyRechargeResponse;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.entity.PaymentCategory;
import com.ureca.snac.money.entity.RechargeStatus;
import com.ureca.snac.money.exception.AlreadyProcessedOrderException;
import com.ureca.snac.money.exception.AmountMismatchException;
import com.ureca.snac.money.exception.OrderNotFoundException;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import com.ureca.snac.payments.TossPaymentsClient;
import com.ureca.snac.payments.dto.TossConfirmResponse;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MoneyRechargeRepository moneyRechargeRepository;
    private final TossPaymentsClient tossPaymentsClient;
    // 토스 통신
    private final WalletService walletService;
    // 지갑 관리
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MoneyRechargeResponse prepareRecharge(MoneyRechargeRequest request, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        // 토스에서 UUID 사라고 그럼 보안과 고유성을 위해서
        String pgOrderId = "snac_order_" + UUID.randomUUID();

        MoneyRecharge newRecharge = MoneyRecharge.builder()
                .member(member)
                .paidAmountWon(request.getAmount())
                .pg(PaymentCategory.TOSS)
                .pgOrderId(pgOrderId)
                .build();

        moneyRechargeRepository.save(newRecharge);

        return MoneyRechargeResponse.create(
                pgOrderId,
                "스낵 머니 " + request.getAmount() + "원 충전",
                request.getAmount(),
                member.getName()
        );
        // 응답 Dto 생성 및 반환
    }

    @Override
    @Transactional
    public void processRechargeSuccess(String paymentKey, String orderId, Long amount) {
        // 시스템 데이터 정합성 확인
        MoneyRecharge recharge = moneyRechargeRepository.findByPgOrderId(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!recharge.getPaidAmountWon().equals(amount.intValue())) {
            throw new AmountMismatchException();
        }

        if (recharge.getStatus() != RechargeStatus.PENDING) {
            throw new AlreadyProcessedOrderException();
        }

        // 토스 페이먼츠에 직접 확인 외부 결제 시스템
        TossConfirmResponse tossConfirmResponse = tossPaymentsClient.confirmPayment(paymentKey, orderId, amount);

        // 비즈니스 로직 실행 머니 입금 하고 주문상태 성공으로 멱등성 확보
        walletService.depositMoney(recharge.getMember().getId(), amount);

        recharge.complete(tossConfirmResponse);
    }
}

