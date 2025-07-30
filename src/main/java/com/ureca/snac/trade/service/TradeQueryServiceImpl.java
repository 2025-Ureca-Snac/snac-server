package com.ureca.snac.trade.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.controller.request.TradeQueryType;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeType;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeQueryService;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ureca.snac.trade.entity.TradeStatus.DATA_SENT;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeQueryServiceImpl implements TradeQueryService {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;

    // 판매 또는 구매에 대한 거래내역을 가져옵니다.
    // TradeSide를 기준으로 BUY, SELL을 구분합니다.
    // 요청한 사이즈보다 1개 더 조회하여 다음페이지가 있는지 체크합니다.
    @Override
    public ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, TradeQueryType tradeQueryType, Long lastTradeId) {
        Member member = findMember(username);

        List<Trade> trades = (side == TradeSide.BUY)
                ? tradeRepository.findTradesByBuyerInfinite(member.getId(), lastTradeId, tradeQueryType,  size + 1)
                : tradeRepository.findTradesBySellerInfinite(member.getId(), lastTradeId, tradeQueryType,  size + 1);

        boolean hasNext = trades.size() > size;

        List<TradeResponse> dto = trades.stream()
                .limit(size)
                .map(t -> TradeResponse.from(t, side))
                .toList();

        return new ScrollTradeResponse(dto, hasNext);
    }

    @Override
    public ProgressTradeCountResponse countSellingProgress(String username) {
        Member seller = findMember(username);

        return new ProgressTradeCountResponse(tradeRepository.countBySellerAndStatusIn(seller,
                List.of(DATA_SENT, PAYMENT_CONFIRMED)));
    }

    @Override
    public ProgressTradeCountResponse countBuyingProgress(String username) {
        Member buyer = findMember(username);

        return new ProgressTradeCountResponse(tradeRepository.countByBuyerAndStatusIn(buyer,
                List.of(DATA_SENT, PAYMENT_CONFIRMED)));
    }

    // 메시지 알림용 trade 반환 메서드
    @Override
    public TradeDto findByTradeId(Long tradeId) {
        return tradeRepository.findById(tradeId)
                .map(TradeDto::from)
                .orElseThrow(TradeNotFoundException::new);
    }

    // 일반 거래에 사용하는 trade 반환 메서드
    @Override
    public TradeResponse getTradeById(Long tradeId, String username) {
        Member member = findMember(username);

        return tradeRepository.findByIdAndParticipant(tradeId, member)
                .map(t -> TradeResponse.from(t, member.getEmail()))
                .orElseThrow(TradeNotFoundException::new);
    }

    @Override
    @Transactional
    public List<TradeDto> findBuyerRealTimeTrade(String buyerUsername) {
        Member member = findMember(buyerUsername);

        return tradeRepository
                .findAllByBuyerAndTradeType(member, TradeType.REALTIME)
                .stream().map(TradeDto::from)
                .toList();
    }

    @Override
    @Transactional
    public List<TradeDto> findSellerRealTimeTrade(String sellerUsername) {
        Member member = findMember(sellerUsername);

        return tradeRepository
                .findAllBySellerAndTradeType(member, TradeType.REALTIME)
                .stream().map(TradeDto::from)
                .toList();
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
