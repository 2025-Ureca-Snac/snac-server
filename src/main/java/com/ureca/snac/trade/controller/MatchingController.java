package com.ureca.snac.trade.controller;

import com.ureca.snac.board.controller.request.CreateRealTimeCardRequest;
import com.ureca.snac.trade.controller.request.BuyerFilterRequest;
import com.ureca.snac.trade.service.MatchingServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingServiceFacade matchingServiceFacade;

    @MessageMapping("/register-filter")
    public void registerBuyerFilter(@Payload BuyerFilterRequest filter, Principal principal) {
        matchingServiceFacade.registerBuyerFilterAndNotify(principal.getName(), filter);
    }

    @MessageMapping("/register-realtime-card")
    public void registerRealtimeCard(@Payload CreateRealTimeCardRequest request, Principal principal) {
        String username = principal.getName();
        matchingServiceFacade.createRealtimeCardAndNotifyBuyers(username, request);
    }
}
