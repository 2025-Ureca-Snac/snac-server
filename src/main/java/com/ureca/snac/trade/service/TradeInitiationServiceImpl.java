package com.ureca.snac.trade.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.board.exception.CardInvalidStatusException;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.exception.TradePaymentMismatchException;
import com.ureca.snac.trade.exception.TradePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeSelfRequestException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeInitiationService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.board.entity.constants.SellStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeInitiationServiceImpl implements TradeInitiationService {
    private final TradeRepository tradeRepository;
    private final TradeSupport tradeSupport;

    // 이벤트 기록 저장
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

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
        Card card = tradeSupport.findLockedCard(claimBuyRequest.getCardId());
        Member seller = tradeSupport.findMember(username);
        Trade trade = tradeRepository
                .findLockedByCardId(claimBuyRequest.getCardId())
                .orElseThrow(TradeNotFoundException::new);

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
     * @param username 요청자
     * @param requiredStatus 거래 유형 (SELLING: 판매글, PENDING: 구매글)
     */
    private Long buildTrade(CreateTradeRequest createTradeRequest, String username, SellStatus requiredStatus) {
        Member member = tradeSupport.findMember(username);
        Wallet wallet = tradeSupport.findLockedWallet(member.getId());
        //-------- Wallet 엔티티 직접 가져오지말고 WalletService DI 하는게 안나은가?-------

        Card card = tradeSupport.findLockedCard(createTradeRequest.getCardId());

        // 카드 상태가 판매 중이 아닌데 판매 거래 요청이 들어오면 예외
        if (requiredStatus == SELLING && card.getSellStatus() != SELLING) {
            throw new CardAlreadyTradingException();
        }

        // 카드 상태 확인
        ensureStatus(card, requiredStatus);

        // 거래 생성 소유자 조건 확인: 판매글 -> 타인, 구매글 -> 본인
        ensureOwnership(card, member, requiredStatus);

        // 결제 금액 검증 (금액 + 포인트 == 카드 가격)
        int totalPay = createTradeRequest.getMoney() + createTradeRequest.getPoint();
//        long totalPay = createTradeRequest.getMoney() + createTradeRequest.getPoint();

        if (card.getPrice() != totalPay)
            throw new TradePaymentMismatchException();

        // 금액 및 포인트 차감
        if (createTradeRequest.getMoney() != 0) {
            wallet.withdrawMoney(createTradeRequest.getMoney());
            // 예를 들면 walletService.withdrawMoney(createTradeRequest.getMoney());
        }
        if (createTradeRequest.getPoint() != 0) {
            wallet.withdrawPoint(createTradeRequest.getPoint());
        }

        // 거래 엔티티 생성 및 저장
        Trade trade = Trade.buildTrade(createTradeRequest.getPoint(), member, member.getPhone(), card, requiredStatus);
        tradeRepository.save(trade);
        
        if (createTradeRequest.getMoney() > 0) {
            long moneyBalanceAfter = wallet.getMoney();
//            long moneyBalanceAfter = walletService.getMoneyBalance(member.getId());
            String title = String.format("%s %dGB 머니 사용", card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithMoney(
                    member.getId(), trade.getId(), title, Long.valueOf(createTradeRequest.getMoney()), moneyBalanceAfter);

            assetHistoryEventPublisher.publish(event);
        }

        if (createTradeRequest.getPoint() > 0) {
            long pointBalanceAfter = wallet.getPoint();
//            long pointBalanceAfter = walletService.getPointBalance(member.getId());
            String title = String.format("%s %dGB 포인트 사용", card.getCarrier().name(), card.getDataAmount());

            AssetChangedEvent event = assetChangedEventFactory.createForBuyWithPoint(
                    member.getId(), trade.getId(), title, Long.valueOf(createTradeRequest.getPoint()), pointBalanceAfter);

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
}
