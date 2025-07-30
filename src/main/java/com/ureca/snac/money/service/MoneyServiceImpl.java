package com.ureca.snac.money.service;

import com.ureca.snac.common.exception.InternalServerException;
import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.money.dto.MoneyRechargePreparedResponse;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeSuccessResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentRecoveryService;
import com.ureca.snac.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.common.BaseCode.PAYMENT_INTERNAL_ERROR;

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

    // 결제 취소 2단계 로직 서비스
    private final PaymentRecoveryService paymentRecoveryService;

    @Override
    @Transactional
    public MoneyRechargePreparedResponse prepareRecharge(MoneyRechargeRequest request, String email) {

        log.info("[머니 충전 준비] 시작. 회원 : {}, 요청 금액 : {}", email, request.getAmount());
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Payment payment = paymentService.preparePayment(member, request.getAmount());

        log.info("[머니 충전 준비] Payment 생성 완료 주문번호 : {}", payment.getOrderId());

        // 응답 Dto 생성 및 반환
        return MoneyRechargePreparedResponse.builder()
                .orderId(payment.getOrderId())
                .orderName("스낵 머니 " + request.getAmount() + "원 충전")
                .amount(request.getAmount())
                .customerName(member.getName())
                .customerEmail(member.getEmail())
                .build();
    }

    @Override
    public MoneyRechargeSuccessResponse processRechargeSuccess(
            String paymentKey, String orderId, Long amount, String email) {

        // 1단계 시작
        log.info("[머니 충전 처리] 시작. 주문 번호 : {}, 요청 금액 : {}", orderId, amount);

        Member member = findMemberByEmail(email);
        Payment payment = findAndValidatePayment(orderId, amount, member);

        TossConfirmResponse tossConfirmResponse =
                paymentGatewayAdapter.confirmPayment(paymentKey, orderId, amount);

        try {
            // 내부 DB 변경 하는 로직 요구
            moneyDepositor.deposit(payment, member, tossConfirmResponse);

        } catch (Exception e) {
            // 머니 충전 누락 실패시 서비스 호출

            log.error("[결제 누락 위험 감지] 내부 DB 처리 실패. 복구 서비스 호출 주문 번호 : {}",
                    orderId);

            paymentRecoveryService.processInternalFailure(payment, e);

            throw new InternalServerException(PAYMENT_INTERNAL_ERROR);
        }

        log.info("[머니 충전 처리 완료] 모든 프로세스 종료");

        return MoneyRechargeSuccessResponse.of(orderId, paymentKey, amount);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Payment findAndValidatePayment(String orderId, Long amount, Member member) {
        log.info("[데이터 정합성 확인] 시작. 주문번호 : {}", orderId);

        Payment payment = paymentRepository.findByOrderIdWithMember(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        payment.validateForConfirmation(member, amount);

        log.info("[데이터 정합성 확인] 모든 검증 통과. 주문번호 : {}", orderId);
        return payment;
    }
}