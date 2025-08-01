package com.ureca.snac.trade.controller;

import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.trade.controller.request.*;
import com.ureca.snac.trade.service.MatchingServiceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MatchingController implements MatchingControllerSwagger {

    private final MatchingServiceFacade matchingServiceFacade;
    private final StringRedisTemplate redisTemplate;

    @MessageMapping("/register-filter")
    public void registerBuyerFilter(@Payload BuyerFilterRequest filter, Principal principal) {
        log.info("[매칭] /register-filter 호출, 사용자: {}, 필터: {}", principal.getName(), filter);
        matchingServiceFacade.registerBuyerFilterAndNotify(principal.getName(), filter);
    }

    @MessageMapping("/register-realtime-card")
    public void registerRealtimeCard(@Payload CreateRealTimeCardRequest request, Principal principal) {
        log.info("[매칭] /register-realtime-card 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.createRealtimeCardAndNotifyBuyers(principal.getName(), request);
    }

    @MessageMapping("/connected-users")
    @SendToUser("/queue/connected-users")
    public Long getConnectedUserCount(Principal principal) {
        log.info("[매칭] /connected-users 호출, 사용자: {}", principal.getName());
        return matchingServiceFacade.getConnectedUserCount();
    }

    @MessageMapping("/trade/create")
    public void createTrade(@Payload CreateRealTimeTradeRequest request, Principal principal) {
        log.info("[거래] /trade/create 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.createTradeFromBuyer(request, principal.getName());
    }

    @MessageMapping("/trade/approve")
    public void approveTrade(@Payload TradeApproveRequest request, Principal principal) {
        log.info("[거래] /trade/approve 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.acceptTrade(request, principal.getName());
    }

    @MessageMapping("/trade/payment")
    public void payTrade(@Payload CreateRealTimeTradePaymentRequest request, Principal principal) {
        log.info("[거래] /trade/payment 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.payTrade(request, principal.getName());
    }

    @MessageMapping("/trade/confirm")
    public void confirmTrade(@Payload ConfirmTradeRequest request, Principal principal) {
        log.info("[거래] /trade/confirm 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.confirmTrade(request, principal.getName());
    }

    @MessageMapping("/filters")
    public void sendAllBuyerFilters(Principal principal) {
        log.info("[매칭] /filters 호출, 사용자: {}", principal.getName());
        matchingServiceFacade.getBuyerFilters(principal.getName());
    }

    // 취소
    @MessageMapping("/filter/remove")
    public void removeBuyerFilter(Principal principal) {
        log.info("[매칭] /filter/remove 호출, 사용자: {}", principal.getName());
        matchingServiceFacade.removeBuyerFilter(principal.getName());
    }

    @MessageMapping("/trade/buy-request/cancel/buyer")
    public void cancelBuyRequestByBuyer(@Payload CancelBuyRequest request, Principal principal) {
        log.info("[매칭] /trade/buy-request/cancel/buyer 호출, 사용자: {}, 카드ID: {}", principal.getName(), request.getCardId());
        matchingServiceFacade.cancelBuyRequestByBuyer(request, principal.getName());
    }

    @MessageMapping("/trade/buy-request/cancel/seller")
    public void cancelBuyRequestBySeller(@Payload CancelBuyRequest request, Principal principal) {
        log.info("[매칭] /trade/buy-request/cancel/seller 호출, 사용자: {}, 카드ID: {}", principal.getName(), request.getCardId());
        matchingServiceFacade.cancelBuyRequestBySeller(request, principal.getName());
    }

    @MessageMapping("/trade/accepted/cancel/buyer")
    public void cancelAcceptedTradeByBuyer(@Payload CancelRealTimeTradeRequest request, Principal principal) {
        log.info("[거래취소] /trade/accepted/cancel/buyer, 사용자: {}, 거래ID: {}", principal.getName(), request.getTradeId());
        matchingServiceFacade.cancelAcceptedTradeByBuyer(request, principal.getName());
    }

    @MessageMapping("/trade/accepted/cancel/seller")
    public void cancelAcceptedTradeBySeller(@Payload CancelRealTimeTradeRequest request, Principal principal) {
        log.info("[거래취소] /trade/accepted/cancel/seller, 사용자: {}, 거래ID: {}", principal.getName(), request.getTradeId());
        matchingServiceFacade.cancelAcceptedTradeBySeller(request, principal.getName());
    }

    @MessageMapping("/trade/payment/cancel/buyer")
    public void cancelPaymentTradeByBuyer(@Payload CancelRealTimeTradeRequest request, Principal principal) {
        log.info("[거래취소] /trade/payment/cancel/buyer, 사용자: {}, 거래ID: {}", principal.getName(), request.getTradeId());
        matchingServiceFacade.cancelPaymentTradeByBuyer(request, principal.getName());
    }

    @MessageMapping("/trade/payment/cancel/seller")
    public void cancelPaymentTradeBySeller(@Payload CancelRealTimeTradeRequest request, Principal principal) {
        log.info("[거래취소] /trade/payment/cancel/seller, 사용자: {}, 거래ID: {}", principal.getName(), request.getTradeId());
        matchingServiceFacade.cancelPaymentTradeBySeller(request, principal.getName());
    }

    @MessageMapping("/trade/dispute")
    public void createDispute(@Payload CreateDisputeRequest request, Principal principal) {
        log.info("[신고] /trade/dispute 호출, 사용자: {}, 요청: {}", principal.getName(), request);
        matchingServiceFacade.createDispute(request, principal.getName());
    }
}
