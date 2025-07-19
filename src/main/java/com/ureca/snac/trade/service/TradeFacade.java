package com.ureca.snac.trade.service;

import com.ureca.snac.auth.service.SnsService;
import com.ureca.snac.board.service.CardService;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.service.interfaces.AttachmentService;
import com.ureca.snac.trade.service.interfaces.TradeInitiationService;
import com.ureca.snac.trade.service.interfaces.TradeProgressService;
import com.ureca.snac.trade.service.interfaces.TradeQueryService;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.support.TradeMessageBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeFacade {

    private final TradeInitiationService tradeInitiationService;
    private final TradeProgressService tradeProgressService;
    private final TradeQueryService tradeQueryService;

    private final AttachmentService attachmentService;
    private final SnsService snsService;
    private final CardService cardService;

    private final TradeMessageBuilder tradeMessageBuilder;

    // === TradeInitiationService === //
    @Transactional
    public Long createSellTrade(CreateTradeRequest createTradeRequest, String username) {
        Long savedTradeId = tradeInitiationService.createSellTrade(createTradeRequest, username);

//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(savedTradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());

        return savedTradeId;
    }

    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        Long tradeId = tradeInitiationService.createBuyTrade(createTradeRequest, username);

//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(tradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());

        return tradeId;
    }

    @Transactional
    public void acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username) {
        Long tradeId = tradeInitiationService.acceptBuyRequest(claimBuyRequest, username);

//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(tradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());
    }

    // === TradeProgressService === //
    @Transactional
    public void sendTradeData(Long tradeId, String username, MultipartFile picture) {
        Long sendTradeId = tradeProgressService.sendTradeData(tradeId, username);

        // 파일 업로드
        attachmentService.upload(tradeId, username, picture);

//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(sendTradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());
    }

    @Transactional
    public void confirmTrade(Long tradeId, String username) {
        Long confirmTradeId = tradeProgressService.confirmTrade(tradeId, username);

//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(confirmTradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());
    }

    @Transactional
    public void cancelTrade(Long tradeId, String username) {
        Long cancelCardId = tradeProgressService.cancelTrade(tradeId, username);
//        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(tradeId);
//        snsService.sendSms(tradeMessageDto.getPhoneList(), tradeMessageDto.getMessage());

        cardService.deleteCardByTrade(cancelCardId);
    }

    public ScrollTradeResponse scrollTrades(String username, TradeSide side, int size, Long lastTradeId) {
        return tradeQueryService.scrollTrades(username, side, size, lastTradeId);
    }

    public ProgressTradeCountResponse countSellingProgress(String username) {
        return tradeQueryService.countSellingProgress(username);
    }

    public ProgressTradeCountResponse countBuyingProgress(String username) {
        return tradeQueryService.countBuyingProgress(username);
    }
}
