package com.ureca.snac.trade.support;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeSupport {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;

    public Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public Wallet findLockedWallet(Long memberId) {
        return walletRepository.findByMemberIdWithLock(memberId).orElseThrow(WalletNotFoundException::new);
    }

    public Card findLockedCard(Long cardId) {
        return cardRepository.findLockedById(cardId).orElseThrow(CardNotFoundException::new);
    }

    public Trade findLockedTrade(Long tradeId) {
        return tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
    }
}
