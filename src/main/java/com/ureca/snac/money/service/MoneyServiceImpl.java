package com.ureca.snac.money.service;

import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentAlreadyProcessedPaymentException;
import com.ureca.snac.payment.exception.PaymentAmountMismatchException;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.exception.PaymentOwnershipMismatchException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    // 머니 입금 DB 처리 담당 서비스
    private final MoneyDepositor moneyDepositor;

    @Override
    @Transactional
    public MoneyRechargeResponse prepareRecharge(MoneyRechargeRequest request, String email) {

        log.info("[머니 충전 준비] 시작. 회원 : {}, 요청 금액 : {}", email, request.getAmount());
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Payment payment = paymentService.preparePayment(member, request.getAmount());

        log.info("[머니 충전 준비] Payment 생성 완료 주문번호 : {}", payment.getOrderId());

        return MoneyRechargeResponse.create(
                payment.getOrderId(),
                "스낵 머니 " + request.getAmount() + "원 충전",
                request.getAmount(),
                member.getName(),
                member.getEmail()
        );
        // 응답 Dto 생성 및 반환
    }

    @Override
    public void processRechargeSuccess(String paymentKey, String orderId, Long amount, String email) {

        // 1단계 시작
        log.info("[머니 충전 처리] 시작. 주문번호 : {}, 요청 금액 : {}", orderId, amount);

        Member member = findMemberByEmail(email);
        Payment payment = validatePayment(orderId, amount, member);

        TossConfirmResponse tossConfirmResponse =
                paymentGatewayAdapter.confirmPayment(paymentKey, orderId, amount);

        moneyDepositor.deposit(payment, member, tossConfirmResponse);

        log.info("[머니 충전 처리 완료] 모든 프로세스 종료");
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    protected Payment validatePayment(String orderId, Long amount, Member member) {
        log.info("[데이터 정합성 확인] 시작. orderId : {}", orderId);

        Payment payment = paymentRepository.findByOrderIdWithMember(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.isAlreadyProcessed()) {
            throw new PaymentAlreadyProcessedPaymentException();
        }

        if (!payment.isOwner(member)) {
            throw new PaymentOwnershipMismatchException();
        }

        if (!payment.isAmount(amount)) {
            throw new PaymentAmountMismatchException();
        }
        log.info("[데이터 정합성 확인] 모든 검증 통과. orderId : {}", orderId);

        return payment;
    }
}