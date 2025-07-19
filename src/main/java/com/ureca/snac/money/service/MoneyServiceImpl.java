package com.ureca.snac.money.service;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
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

    // 이벤트 발행
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;

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

        // 1단계 시작
        log.info("[머니 충전 처리] 시작. 주문번호 : {}, 요청 금액 : {}", orderId, amount);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        // 2단계 시스템 데이터 정합성 확인
        log.info("[데이터 정합성 확인] 우리 시스템의 Payment 정보 조회. 주문번호 : {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentRedirectException(PAYMENT_NOT_FOUND));

        if (payment.isAlreadyProcessed()) {
            log.warn("[중복 처리 시도] 이미 처리된 결제입니다. 주문번호: {}", orderId);
            throw new PaymentRedirectException(ALREADY_PROCESSED_PAYMENT);
        }

        // 결제 멤버랑 현재 요청자 검토
        if (payment.validateOwner(member)) {
            log.error("[결제 소유권 불일치] 주문번호 : {}, 결제한 멤버 ID  : {}, 요청 ID : {}", orderId, payment.getMember().getId(), member.getId());
            throw new PaymentRedirectException(PAYMENT_OWNERSHIP_MISMATCH);
        }

        if (!payment.isAmount(amount)) {
            log.error("[결제 금액 불일치] 주문번호 : {}, DB 금액 : {}, 요청 금액 : {}", orderId, payment.getAmount(), amount);
            throw new PaymentRedirectException(AMOUNT_MISMATCH);
        }
        log.info("[데이터 정합성 확인] 모든 검증 통과. 주문번호 : {}", orderId);

        // 3단계 스 페이먼츠에 직접 확인 외부 결제 시스템
        log.info("[외부 API 호출] 토스 페이먼츠 결제 승인 요청 시작. 주문번호 : {}", orderId);

        TossConfirmResponse tossConfirmResponse = tossPaymentsClient.confirmPayment(paymentKey, orderId, amount);
        log.info("[외부 API 응답] 토스페이먼츠로부터 결제 승인 성공.");

        // 4단꼐 DB 업데이트
        payment.complete(tossConfirmResponse.paymentKey(), tossConfirmResponse.method(), tossConfirmResponse.approvedAt());
        log.info("[데이터] Payment 상태 SUCCESS 변경");

        MoneyRecharge recharge = MoneyRecharge.create(payment);
        moneyRechargeRepository.save(recharge);
        log.info("[데이터] MoneyRecharge 기록 생성 완료. 충전 ID : {}", recharge.getId());

        // 5단계 지갑 입금 비즈니스 로직 실행 머니 입금 하고 주문상태 성공으로 멱등성 확보
        log.info("[지갑 입금] 실제 머니 입금 시작. 회원 ID : {}, 금액 : {}", member.getId(), payment.getAmount());
        Long balanceAfter = walletService.depositMoney(recharge.getMember().getId(), payment.getAmount());
        log.info("[머니 충전 처리] 최종 완료 회원 ID : {}, 최종잔액 : {}", payment.getMember().getId(), balanceAfter);

        // 6단계 이벤트 발행
        AssetChangedEvent event = AssetChangedEvent.builder()
                .memberId(member.getId())
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.RECHARGE)
                .amount(payment.getAmount())
                .balanceAfter(balanceAfter)
                .title("스낵 머니 충전")
                .sourceDomain("MONEY_RECHARGE")
                .sourceId(recharge.getId())
                .build();

        assetHistoryEventPublisher.publish(event);
        log.info("[이벤트 발행] 자산 내역 기록을 위한 이벤트 발행 성공. 회원 ID : {}", member.getId());
        log.info("[머니 충전 처리 완료] 모든 프로세스 종료");
    }
}