package com.ureca.snac.dev.support;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DevDataSupport {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final MoneyRechargeRepository moneyRechargeRepository;
    private final CardRepository cardRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public RechargeContext prepareRecharge(String email, Long amount) {
        Member member = findMemberByEmail(email);
        Payment fakePayment = createAndSaveFakePayment(member, amount);
        MoneyRecharge recharge = createAndSaveRecharge(fakePayment);
        return new RechargeContext(member, fakePayment, recharge);
    }

    @Transactional
    public TradeCompletionContext prepareCompletedTrade(
            String cardOwnerEmail, String counterEmail, CardCategory category, Carrier carrier,
            Integer dataAmount, Long moneyAmountToUse, Long pointAmountToUse) {

        Member cardOwner = findMemberByEmail(cardOwnerEmail);
        Member counter = findMemberByEmail(counterEmail);

        Member seller = (category == CardCategory.SELL) ? cardOwner : counter;
        Member buyer = (category == CardCategory.SELL) ? counter : cardOwner;

        Long totalPrice = (moneyAmountToUse != null ? moneyAmountToUse : 0L) +
                (pointAmountToUse != null ? pointAmountToUse : 0L);

        Card fakeCard = createAndSaveFakeCard(cardOwner, carrier, dataAmount,
                totalPrice, category);

        Trade fakeTrade = createAndSaveFakeTrade(fakeCard, seller, buyer);

        return new TradeCompletionContext(seller, buyer, fakeCard, fakeTrade);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Payment createAndSaveFakePayment(Member member, Long amount) {
        Payment fakePayment = Payment.createForDev(member, amount);
        return paymentRepository.save(fakePayment);
    }

    private MoneyRecharge createAndSaveRecharge(Payment payment) {
        MoneyRecharge recharge = MoneyRecharge.create(payment);
        return moneyRechargeRepository.save(recharge);
    }

    private Card createAndSaveFakeCard(Member owner, Carrier carrier, Integer dataAmount
            , Long price, CardCategory category) {
        Card fakeCard = Card.createFake(owner, carrier, dataAmount,
                price, category);
        return cardRepository.save(fakeCard);
    }

    private Trade createAndSaveFakeTrade(Card card, Member seller, Member buyer) {
        Trade fakeTrade = Trade.createFake(card, seller, buyer);
        return tradeRepository.save(fakeTrade);
    }

    public record RechargeContext(Member member, Payment payment, MoneyRecharge recharge) {

    }

    public record TradeCompletionContext(Member seller, Member buyer, Card card, Trade trade) {

    }
}
