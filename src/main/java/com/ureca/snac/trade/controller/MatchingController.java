package com.ureca.snac.trade.controller;

import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.board.exception.CardAlreadyTradingException;
import com.ureca.snac.trade.controller.request.*;
import com.ureca.snac.trade.service.MatchingServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingServiceFacade matchingServiceFacade;
    private final StringRedisTemplate redisTemplate;

    @MessageMapping("/register-filter")
    public void registerBuyerFilter(@Payload BuyerFilterRequest filter, Principal principal) {
        matchingServiceFacade.registerBuyerFilterAndNotify(principal.getName(), filter);
    }

    @MessageMapping("/register-realtime-card")
    public void registerRealtimeCard(@Payload CreateRealTimeCardRequest request, Principal principal) {
        String username = principal.getName();
        matchingServiceFacade.createRealtimeCardAndNotifyBuyers(username, request);
    }

    @MessageMapping("/connected-users")
    @SendToUser("/queue/connected-users")
    public Long getConnectedUserCount(Principal principal) {
        return matchingServiceFacade.getConnectedUserCount();
    }

    @MessageMapping("/trade/create")
    public void createTrade(@Payload CreateRealTimeTradeRequest request, Principal principal) {
        String username = principal.getName();

        matchingServiceFacade.createTradeFromBuyer(request, username);
    }

    @MessageMapping("/trade/approve")
    public void approveTrade(@Payload TradeApproveRequest request, Principal principal) {
        String username = principal.getName();
        matchingServiceFacade.acceptTrade(request, username);
    }

    @MessageMapping("/trade/payment")
    public void payTrade(@Payload CreateRealTimeTradePaymentRequest request, Principal principal) {
        String username = principal.getName();
        matchingServiceFacade.payTrade(request, username);
    }

    @MessageExceptionHandler(CardAlreadyTradingException.class)
    @SendToUser("/queue/errors")
    public String handleCardAlreadyTradingException(CardAlreadyTradingException ex) {
        return ex.getMessage();
    }
}
