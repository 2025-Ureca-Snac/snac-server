package com.ureca.snac.dev.support;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DevDataSupport {
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final TradeRepository tradeRepository;

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

    public record TradeCompletionContext(Member seller, Member buyer, Card card, Trade trade) {

    }
}
