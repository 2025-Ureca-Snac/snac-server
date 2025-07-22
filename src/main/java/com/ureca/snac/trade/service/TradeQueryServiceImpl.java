package com.ureca.snac.trade.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeQueryService;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import com.ureca.snac.trade.support.TradeSupport;
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
    private final TradeSupport tradeSupport;

    @Override
    public ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, Long lastTradeId) {
        Member member = tradeSupport.findMember(username);

        List<Trade> trades = (side == TradeSide.BUY)
                ? tradeRepository.findTradesByBuyerInfinite(member.getId(), lastTradeId, size + 1)
                : tradeRepository.findTradesBySellerInfinite(member.getId(), lastTradeId, size + 1);

        boolean hasNext = trades.size() > size;

        List<TradeResponse> dto = trades.stream()
                .limit(size)
                .map(t -> TradeResponse.from(t, side))
                .toList();

        return new ScrollTradeResponse(dto, hasNext);
    }

    @Override
    public ProgressTradeCountResponse countSellingProgress(String username) {
        Member seller = tradeSupport.findMember(username);

        return new ProgressTradeCountResponse(tradeRepository.countBySellerAndStatusIn(seller,
                List.of(DATA_SENT, PAYMENT_CONFIRMED)));
    }

    @Override
    public ProgressTradeCountResponse countBuyingProgress(String username) {
        Member buyer = tradeSupport.findMember(username);

        return new ProgressTradeCountResponse(tradeRepository.countByBuyerAndStatusIn(buyer,
                List.of(DATA_SENT, PAYMENT_CONFIRMED)));
    }

    @Override
    public TradeDto findByTradeId(Long tradeId) {
        return tradeRepository.findById(tradeId)
                .map(TradeDto::from)
                .orElseThrow(TradeNotFoundException::new);
    }
}
