package com.ureca.snac.trade.service;

import com.ureca.snac.favorite.repository.FavoriteRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.trade.controller.request.TradeQueryType;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeType;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.TradeQueryService;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.service.response.TradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ureca.snac.trade.entity.TradeStatus.DATA_SENT;
import static com.ureca.snac.trade.entity.TradeStatus.PAYMENT_CONFIRMED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeQueryServiceImpl implements TradeQueryService {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final TradeCancelRepository tradeCancelRepository;
    private final FavoriteRepository favoriteRepository;

    // 판매 또는 구매에 대한 거래내역을 가져옵니다.
    // TradeSide를 기준으로 BUY, SELL을 구분합니다.
    // 요청한 사이즈보다 1개 더 조회하여 다음페이지가 있는지 체크합니다.
    @Override
    public ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, TradeQueryType tradeQueryType, Long lastTradeId) {
        if (username == null) {
            return new ScrollTradeResponse(Collections.emptyList(), false);
        }
        Member member = findMember(username);

        List<Trade> trades = (side == TradeSide.BUY)
                ? tradeRepository.findTradesByBuyerInfinite(member.getId(), lastTradeId, tradeQueryType, size + 1)
                : tradeRepository.findTradesBySellerInfinite(member.getId(), lastTradeId, tradeQueryType, size + 1);

        boolean hasNext = trades.size() > size;
        List<Trade> page = hasNext ? trades.subList(0, size) : trades;

        // 단골 여부 확인데이터
        Set<Long> favoritePartnerIds = Collections.emptySet();

        if (!page.isEmpty()) {
            Set<Long> partnerIdSet = new HashSet<>();
            for (Trade trade : page) {
                // 내가 구매자 이면 상대방이 판매자
                if (trade.getBuyer().getId().equals(member.getId())) {
                    // 구매글 처리
                    if (trade.getSeller() != null) {
                        partnerIdSet.add(trade.getSeller().getId());
                    }
                } else {
                    partnerIdSet.add(trade.getBuyer().getId());
                }
            }
            if (!partnerIdSet.isEmpty()) {
                List<Long> partnerIds = new ArrayList<>(partnerIdSet);
                favoritePartnerIds = favoriteRepository.findFavoriteToMemberIdsByFromMember(member, partnerIds);
            }
        }

        // 취소 요청 정보
        List<Long> tradesIds = new ArrayList<>();
        for (Trade t : page) {
            tradesIds.add(t.getId());
        }

        List<TradeCancelRepository.TradeCancelSummary> summaries =
                tradeCancelRepository.findCancelSummaryByTradeIds(tradesIds);

        Map<Long, TradeCancelRepository.TradeCancelSummary> cancelMap = new HashMap<>();

        for (TradeCancelRepository.TradeCancelSummary summary : summaries) {
            cancelMap.put(summary.getTradeId(), summary);
        }

        // DTO 생성
        List<TradeResponse> dtoList = new ArrayList<>();
        for (Trade t : page) {
            Long partnerId = null;
            String partnerNickname = null;

            if (t.getBuyer().getId().equals(member.getId())) {
                if (t.getSeller() != null) {
                    partnerId = t.getSeller().getId();
                    partnerNickname = t.getSeller().getNickname();
                }
            } else {
                partnerId = t.getBuyer().getId();
                partnerNickname = t.getBuyer().getNickname();
            }

            boolean isPartnerFavorite = (partnerId != null) && favoritePartnerIds.contains(partnerId);

            TradeCancelRepository.TradeCancelSummary cancel = cancelMap.get(t.getId());

            TradeResponse dto = TradeResponse.from(
                    t, username, isPartnerFavorite, cancel, partnerId, partnerNickname);
            dtoList.add(dto);
        }

        return new ScrollTradeResponse(dtoList, hasNext);
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

//        return tradeRepository.findByIdAndParticipant(tradeId, member)
//                .map(t -> TradeResponse.from(t, member.getEmail()))
//                .orElseThrow(TradeNotFoundException::new);

        Trade trade = tradeRepository.findByIdAndParticipant(tradeId, member)
                .orElseThrow(TradeNotFoundException::new);

        Long partnerId = null;
        String partnerNickname = null;

        if (trade.getBuyer().getId().equals(member.getId())) {
            if (trade.getSeller() != null) {
                partnerId = trade.getSeller().getId();
                partnerNickname = trade.getSeller().getNickname();
            }
        } else {
            partnerId = trade.getBuyer().getId();
            partnerNickname = trade.getBuyer().getNickname();
        }

        boolean isPartnerFavorite = false;
        if (partnerId != null) {
            isPartnerFavorite =
                    favoriteRepository.existsByFromMemberIdAndToMemberId(member.getId(), partnerId);
        }

        TradeCancelRepository.TradeCancelSummary cancel =
                tradeCancelRepository.findSummaryByTradeId(tradeId).orElse(null);

        return TradeResponse.from(
                trade, username, isPartnerFavorite, cancel, partnerId, partnerNickname
        );
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

    @Override
    @Transactional
    public TradeDto onBuyerDataSentRealTime(String username) {
        Member member = findMember(username);

        return tradeRepository.findByBuyerAndStatus(member, DATA_SENT)
                .map(TradeDto::from)
                .orElse(null);
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
