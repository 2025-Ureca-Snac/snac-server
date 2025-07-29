package com.ureca.snac.trade.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.*;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateRealTimeTradePaymentRequest;
import com.ureca.snac.trade.controller.request.CreateRealTimeTradeRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.*;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeInitiationService;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.board.entity.constants.SellStatus.*;
import static com.ureca.snac.trade.entity.TradeStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeInitiationServiceImpl implements TradeInitiationService {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;

    private final WalletService walletService;
    // 이벤트 기록 저장
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    @Override
    @Transactional
    public Long acceptRealTimeTrade(Long tradeId, String username) {
        Member member = findMember(username);
        Trade trade = findLockedTrade(tradeId);
        Card card = findLockedCard(trade.getCardId());

        // 카드는 판매 상태이여야 함
        if (card.getSellStatus() != SELLING) {
            throw new CardInvalidStatusException();
        }

        // 거래요청 상태이여야 함
        if (trade.getStatus() != BUY_REQUESTED) {
            throw new TradeStatusMismatchException();
        }

        // 판매자만 수락할 수 있어야 함
        if (trade.getSeller() != member) {
            throw new TradePermissionDeniedException();
        }

        trade.changeStatus(ACCEPTED);
        card.changeSellStatus(TRADING);

        return trade.getId();
    }

    @Override
    @Transactional
    public Long payRealTimeTrade(CreateRealTimeTradePaymentRequest createRealTimeTradePaymentRequest, String username) {
        // 1. 거래 조회 (락 걸어서)
        Member member = findMember(username);
        Trade trade = findLockedTrade(createRealTimeTradePaymentRequest.getTradeId());
        Card card = findLockedCard(trade.getCardId());
        Wallet wallet = findLockedWallet(member.getId());

        // 카드는 거래 상태이어야 함
        if (card.getSellStatus() != TRADING) {
            throw new CardInvalidStatusException();
        }

        // 거래는 수락 상태이여야 함
        if (trade.getStatus() != ACCEPTED) {
            throw new TradeStatusMismatchException();
        }

        // 구매자만 결제할 수 있어야 함
        if (trade.getBuyer() != member) {
            throw new TradePermissionDeniedException();
        }

        long moneyToUse = createRealTimeTradePaymentRequest.getMoney();
        long pointToUse = createRealTimeTradePaymentRequest.getPoint();
        long totalPay = moneyToUse + pointToUse;

        if (trade.getPriceGb() != totalPay)
            throw new TradePaymentMismatchException();

        // 1.결제 먼저
        long moneyBalanceAfter = -1L;

        // 금액 및 포인트 차감
        if (moneyToUse > 0) {
            moneyBalanceAfter =
                    walletService.withdrawMoney(member.getId(), moneyToUse);
            // wallet.withdrawMoney(createTradeRequest.getMoney());
        }

        long pointBalanceAfter = -1L;
        if (pointToUse > 0) {
            pointBalanceAfter =
                    walletService.withdrawPoint(member.getId(), pointToUse);
//            wallet.withdrawPoint(createTradeRequest.getPoint());
        }

        trade.changePoint(createRealTimeTradePaymentRequest.getPoint());
        trade.changeStatus(PAYMENT_CONFIRMED);

        // 3 기록
        if (moneyToUse > 0) {
            String title = String.format("%s %dGB 머니 사용",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithMoney(
                    member.getId(), trade.getId(), title, moneyToUse, moneyBalanceAfter);

            assetHistoryEventPublisher.publish(event);
        }

        if (pointToUse > 0) {
            String title = String.format("%s %dGB 포인트 사용",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithPoint(
                    member.getId(), trade.getId(), title, pointToUse, pointBalanceAfter);

            assetHistoryEventPublisher.publish(event);
        }

        return trade.getId();
    }

    @Override
    @Transactional
    public Long createRealTimeTrade(CreateRealTimeTradeRequest request, String username) {
        Member member = findMember(username);
        Card card = findLockedCard(request.getCardId());

        // 실시간 판매 카드가 아닌 경우
        if (card.getCardCategory() != CardCategory.REALTIME_SELL) {
            throw new NotRealTimeSellCardException();
        }

        // 카드는 판매 상태이여야 함
        if (card.getSellStatus() == TRADING) {
            throw new CardAlreadyTradingException();
        }
        if (card.getSellStatus() == SOLD_OUT) {
            throw new CardAlreadySoldOutException();
        }

        // 카드 등록자와 달라야 함
        if (card.getMember() == member) {
            throw new TradeSelfRequestException();
        }

        // 거래 엔티티 생성 및 저장
        Trade trade = Trade.buildTrade(member, member.getPhone(), card);
        tradeRepository.save(trade);

        // 카드 상태 변경 (Trading)
//        card.changeSellStatus(TRADING);

        return trade.getId();
    }

    /**
     * 판매 거래를 생성합니다.
     */
    @Override
    @Transactional
    public Long createSellTrade(CreateTradeRequest createTradeRequest, String username) {
        return buildTrade(createTradeRequest, username, SELLING);
    }

    /**
     * 구매 거래를 생성합니다.
     */
    @Override
    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        return buildTrade(createTradeRequest, username, PENDING);
    }

    /**
     * 구매자가 판매 요청을 수락합니다.
     */
    @Override
    @Transactional
    public Long acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username) {
        Member seller = findMember(username);
        Trade trade = tradeRepository
                .findLockedByCardId(claimBuyRequest.getCardId())
                .orElseThrow(TradeNotFoundException::new);
        Card card = findLockedCard(claimBuyRequest.getCardId());

        // 판매 가능 상태가 아니라면 이미 거래 중으로 간주
        if (card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        // 본인이 판매를 수락하는 것은 허용하지 않음
        if (trade.getBuyer() == seller) {
            throw new TradeSelfRequestException();
        }

        // 거래에 판매자 등록 및 카드 상태 변경
        trade.changeSeller(seller);
        card.changeSellStatus(TRADING);

        return trade.getId();
    }

    // === private helper === //

    /**
     * 거래 객체를 생성하고, 결제 금액을 차감한 후 저장합니다.
     *
     * @param createTradeRequest 거래 요청 정보
     * @param username           요청자
     * @param requiredStatus     거래 유형 (SELLING: 판매글, PENDING: 구매글)
     */
    private Long buildTrade(CreateTradeRequest createTradeRequest, String username, SellStatus requiredStatus) {
        Member member = findMember(username);
        Card card = findLockedCard(createTradeRequest.getCardId());

        // 카드 상태가 판매 중이 아닌데 판매 거래 요청이 들어오면 예외
        if (requiredStatus == SELLING && card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        // 카드 상태 확인
        ensureStatus(card, requiredStatus);

        // 거래 생성 소유자 조건 확인: 판매글 -> 타인, 구매글 -> 본인
        ensureOwnership(card, member, requiredStatus);

        // 결제 금액 검증 (금액 + 포인트 == 카드 가격)
        long moneyToUse = createTradeRequest.getMoney();
        long pointToUse = createTradeRequest.getPoint();
        long totalPay = moneyToUse + pointToUse;

        if (card.getPrice() != totalPay) {
            throw new TradePaymentMismatchException();
        }

        // 1.결제 먼저
        long moneyBalanceAfter = -1L;

        // 금액 및 포인트 차감
        if (moneyToUse > 0) {
            moneyBalanceAfter =
                    walletService.withdrawMoney(member.getId(), moneyToUse);
            // wallet.withdrawMoney(createTradeRequest.getMoney());
        }

        long pointBalanceAfter = -1L;
        if (pointToUse > 0) {
            pointBalanceAfter =
                    walletService.withdrawPoint(member.getId(), pointToUse);
//            wallet.withdrawPoint(createTradeRequest.getPoint());
        }

        // 2, 거래 생성
        // 거래 엔티티 생성 및 저장
        Trade trade = Trade.buildTrade((int) pointToUse, member, member.getPhone(), card, requiredStatus);
        tradeRepository.save(trade);

        // 3 기록
        if (moneyToUse > 0) {
            String title = String.format("%s %dGB 머니 사용",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithMoney(
                    member.getId(), trade.getId(), title, moneyToUse, moneyBalanceAfter);

            assetHistoryEventPublisher.publish(event);
        }

        if (pointToUse > 0) {
            String title = String.format("%s %dGB 포인트 사용",
                    card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithPoint(
                    member.getId(), trade.getId(), title, pointToUse, pointBalanceAfter);

            assetHistoryEventPublisher.publish(event);
        }

        // 카드 상태 변경 (판매글이면 TRADING, 구매글이면 SELLING)
        card.changeSellStatus(requiredStatus == SELLING ? TRADING : SELLING);

        return trade.getId();
    }

    /**
     * 카드의 현재 상태가 거래 요청 조건과 일치하는지 확인합니다.
     * (예: 판매 중인 카드에만 판매 요청 가능)
     */
    private void ensureStatus(Card card, SellStatus sellStatus) {
        if (card.getSellStatus() != sellStatus) {
            throw new CardInvalidStatusException();
        }
    }

    /**
     * 거래 요청자가 카드 소유자인지 여부에 따라 허용 여부를 판단합니다.
     * - 판매 거래 생성 시: 타인의 카드만 가능
     * - 구매 거래 생성 시: 자신의 카드만 가능
     */
    private void ensureOwnership(Card card, Member member, SellStatus requiredStatus) {
        boolean isOwner = card.getMember().equals(member);

        if (requiredStatus == SELLING && isOwner) {
            throw new TradeSelfRequestException();
        }

        if (requiredStatus == PENDING && !isOwner) {
            throw new TradePermissionDeniedException();
        }
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private Wallet findLockedWallet(Long memberId) {
        return walletRepository.findByMemberIdWithLock(memberId).orElseThrow(WalletNotFoundException::new);
    }

    private Card findLockedCard(Long cardId) {
        return cardRepository.findLockedById(cardId).orElseThrow(CardNotFoundException::new);
    }

    private Trade findLockedTrade(Long tradeId) {
        return tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
    }
}
