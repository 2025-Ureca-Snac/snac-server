package com.ureca.snac.money.service;

import com.ureca.snac.infra.TossPaymentsClient;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.money.dto.MoneyRechargeRequest;
import com.ureca.snac.money.dto.MoneyRechargeResponse;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentRedirectException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentService;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.common.BaseCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MemberRepository memberRepository;
    private final MoneyRechargeRepository moneyRechargeRepository;
    private final PaymentRepository paymentRepository;

    // 지갑 관리
    private final WalletService walletService;
    private final PaymentService paymentService;

    // 토스 통신
    private final TossPaymentsClient tossPaymentsClient;

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
    @Transactional
    public void processRechargeSuccess(String paymentKey, String orderId, Long amount, String email) {
        log.info("[머니 충전 처리] 시작. 주문번호 : {}", orderId);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        // 시스템 데이터 정합성 확인
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentRedirectException(PAYMENT_NOT_FOUND));

        if (payment.isAlreadyProcessed()) {
            log.warn("[중복 처리 시도] 이미 처리된 결제입니다. 주문번호: {}", orderId);
            throw new PaymentRedirectException(ALREADY_PROCESSED_PAYMENT);
        }

        // 결제 멤버랑 현재 요청자 검토
        if (payment.validateOwner(member)) {
            log.error("[에러] 결제 소유권 불일치, 주문번호 : {}, 결제한 멤버 ID  : {}, 요청 ID : {}", orderId, payment.getMember().getId(), member.getId());
            throw new PaymentRedirectException(PAYMENT_OWNERSHIP_MISMATCH);
        }

        if (!payment.isAmount(amount)) {
            log.error("[에러] 결제 금액 불일치, 주문번호 : {}, DB 금액  : {}, 요청 금액 : {}", orderId, payment.getAmount(), amount);
            throw new PaymentRedirectException(AMOUNT_MISMATCH);
        }

        log.info("[외부 API] 토스 페이먼츠 결제 승인 요청 시작. 주문번호 : {}", orderId);

        // 토스 페이먼츠에 직접 확인 외부 결제 시스템
        TossConfirmResponse tossConfirmResponse = tossPaymentsClient.confirmPayment(paymentKey, orderId, amount);

        payment.complete(tossConfirmResponse.paymentKey(), tossConfirmResponse.method(), tossConfirmResponse.approvedAt());
        log.info("[데이터] Payment 상태 SUCCESS 변경");

        MoneyRecharge recharge = MoneyRecharge.create(payment);
        moneyRechargeRepository.save(recharge);
        log.info("[데이터] MoneyRecharge 기록 생성 완료. 충전 ID : {}", recharge.getId());

        // 비즈니스 로직 실행 머니 입금 하고 주문상태 성공으로 멱등성 확보
        walletService.depositMoney(recharge.getMember().getId(), payment.getAmount());
        log.info("[머니 충전 처리] 최종 완료 회원 ID : {}, 금액 : {}", payment.getMember().getId(), payment.getAmount());
    }
}