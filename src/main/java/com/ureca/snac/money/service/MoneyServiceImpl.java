package com.ureca.snac.money.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.money.dto.request.MoneyRechargeRequest;
import com.ureca.snac.money.dto.response.MoneyRechargeResponse;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.entity.PaymentCategory;
import com.ureca.snac.money.entity.RechargeStatus;
import com.ureca.snac.money.exception.AlreadyProcessedOrderException;
import com.ureca.snac.money.exception.AmountMismatchException;
import com.ureca.snac.money.exception.OrderNotFoundException;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MoneyRechargeRepository moneyRechargeRepository;

    @Override
    @Transactional
    public MoneyRechargeResponse prepareRecharge(MoneyRechargeRequest request, Member member) {
        // 토스에서 UUID 사라고 그럼 보안과 고유성을 위해서
        String pgOrderId = "snac_order_" + UUID.randomUUID();

        MoneyRecharge newRecharge = MoneyRecharge.builder()
                .member(member)
                .paidAmountWon(request.getAmount())
                .pg(PaymentCategory.TOSS)
                .pgOrderId(pgOrderId)
                .status(RechargeStatus.PENDING)
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
        MoneyRecharge recharge = moneyRechargeRepository.findByPgOrderId(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!recharge.getPaidAmountWon().equals(amount.intValue())) {
            throw new AmountMismatchException();
        }

        if (recharge.getStatus() != RechargeStatus.PENDING) {
            throw new AlreadyProcessedOrderException();
        }

        // TODO
        // 토스 페이먼츠 결제 승인 API
    }
}

