package com.ureca.snac.dev.service;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.SourceDomain;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.dev.dto.DevCancelRechargeRequest;
import com.ureca.snac.dev.dto.DevForceTradeCompleteRequest;
import com.ureca.snac.dev.dto.DevPointGrantRequest;
import com.ureca.snac.dev.dto.DevRechargeRequest;
import com.ureca.snac.dev.support.DevDataSupport;
import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.money.service.MoneyDepositor;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.payment.service.PaymentService;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("!prod")
@Slf4j
@Service
@RequiredArgsConstructor
public class DevToolServiceImpl implements DevToolService {

    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;
    private final MoneyDepositor moneyDepositor;
    private final DevDataSupport devDataSupport;
    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;

    @Override
    @Transactional
    public Long forceRecharge(DevRechargeRequest request) {
        log.info("[개발용 머니 충전] 시작. 이메일 : {}, 금액 : {}",
                request.email(), request.amount());

        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberNotFoundException::new);

        Payment payment = paymentService.preparePayment(member, request.amount());

        TossConfirmResponse fakeConfirmResponse =
                paymentGatewayAdapter.confirmPayment(
                        "dev_payment_key_" + System.currentTimeMillis(),
                        payment.getOrderId(),
                        payment.getAmount()
                );

        moneyDepositor.deposit(payment, member, fakeConfirmResponse);

        log.info("[개발용 머니 충전] 완료. 생성된 Payment Id : {}", payment.getId());
        return payment.getId();
    }

    @Override
    @Transactional
    public void grantPoint(DevPointGrantRequest request) {
        log.info("[개발용 포인트 지급] 시작. 이메일 : {}, 양 : {}, 이유 : {}",
                request.email(), request.amount(), request.reason());

        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberNotFoundException::new);

        walletService.depositPoint(member.getId(), request.amount());
        long balanceAfter = walletService.getPointBalance(member.getId());

        publishEvent(
                member.getId(),
                AssetType.POINT,
                TransactionType.DEPOSIT,
                TransactionCategory.EVENT,
                request.amount(),
                balanceAfter,
                request.reason(),
                SourceDomain.EVENT,
                member.getId()
        );

        log.info("[개발용 포인트 지급] 완료.");
    }

    @Override
    @Transactional
    public void forceCancelRecharge(DevCancelRechargeRequest request) {
        log.info("[개발용 충전 취소] 시작. Payment ID : {}, 사유 : {}",
                request.paymentId(), request.reason());

        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(PaymentNotFoundException::new);

        paymentService.cancelPayment(
                payment.getPaymentKey(),
                request.reason(),
                payment.getMember().getEmail()
        );

        log.info("[개발용 충전 취소] 취소 서비스 완료.");
    }


    @Override
    @Transactional
    public Long forceTradeComplete(DevForceTradeCompleteRequest request) {
        log.info("[개발용 거래 완료] 시작.");

        DevDataSupport.TradeCompletionContext ctx =
                devDataSupport.prepareCompletedTrade(request.cardOwnerEmail(), request.counterEmail(),
                        request.cardCategory(), request.carrier(), request.dataAmount(),
                        request.moneyAmountToUse(), request.pointAmountToUse()
                );

        walletService.withdrawComposite(ctx.buyer().getId(), request.moneyAmountToUse(), request.pointAmountToUse());

        long sellerMoneyBalanceAfter = (request.moneyAmountToUse() > 0) ?
                walletService.depositMoney(ctx.seller().getId(), request.moneyAmountToUse()) :
                walletService.getMoneyBalance(ctx.seller().getId());

        publishTradeEvents(ctx, request.moneyAmountToUse(), request.pointAmountToUse(), sellerMoneyBalanceAfter);

        log.info("[개발용 거래 완료] 완료. 생성된 Trade ID : {}", ctx.trade().getId());

        return ctx.trade().getId();
    }

    private void publishTradeEvents(DevDataSupport.TradeCompletionContext ctx, long moneyUsed,
                                    long pointUsed, long sellerMoneyBalance) {

        String generateTitle = String.format("%s %dGB", ctx.card().getCarrier().name(), ctx.card().getDataAmount());

        long buyerMoneyBalance = walletService.getMoneyBalance(ctx.buyer().getId());
        long buyerPointBalance = walletService.getPointBalance(ctx.buyer().getId());

        if (moneyUsed > 0) {
            publishEvent(ctx.buyer().getId(), AssetType.MONEY, TransactionType.WITHDRAWAL, TransactionCategory.BUY, moneyUsed,
                    buyerMoneyBalance, generateTitle + " 머니 사용 ", SourceDomain.TRADE, ctx.trade().getId());
            publishEvent(ctx.seller().getId(), AssetType.MONEY, TransactionType.DEPOSIT, TransactionCategory.SELL, moneyUsed,
                    sellerMoneyBalance, generateTitle + " 판매 대금 ", SourceDomain.TRADE, ctx.trade().getId());
        }
        if (pointUsed > 0) {
            publishEvent(ctx.buyer().getId(), AssetType.POINT, TransactionType.WITHDRAWAL, TransactionCategory.POINT_USAGE, pointUsed,
                    buyerPointBalance, generateTitle + " 포인트 사용 ", SourceDomain.TRADE, ctx.trade().getId());
        }
    }

    private void publishEvent(Long memberId, AssetType assetType, TransactionType transactionType,
                              TransactionCategory category, Long amount, Long balanceAfter,
                              String title, SourceDomain domain, Long sourceId) {
        assetHistoryEventPublisher.publish(AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(assetType)
                .transactionType(transactionType)
                .category(category)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(title)
                .sourceDomain(domain)
                .sourceId(sourceId)
                .build()
        );
    }
}
