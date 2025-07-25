package com.ureca.snac.trade.service;

import com.ureca.snac.board.service.CardService;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.trade.controller.request.ClaimBuyRequest;
import com.ureca.snac.trade.controller.request.CreateTradeRequest;
import com.ureca.snac.trade.dto.TradeMessageDto;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.CancelReason;
import com.ureca.snac.trade.service.interfaces.*;
import com.ureca.snac.trade.service.response.ProgressTradeCountResponse;
import com.ureca.snac.trade.service.response.ScrollTradeResponse;
import com.ureca.snac.trade.support.TradeMessageBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final CardService cardService;

    private final TradeMessageBuilder tradeMessageBuilder;
    private final RabbitTemplate rabbitTemplate;

    private final TradeCancelService tradeCancelService;

    // === TradeInitiationService === //
    @Transactional
    public Long createSellTrade(CreateTradeRequest createTradeRequest, String username) {
        Long savedTradeId = tradeInitiationService.createSellTrade(createTradeRequest, username);

        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(savedTradeId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_TRADE_ROUTING_KEY, tradeMessageDto);

        return savedTradeId;
    }

    @Transactional
    public Long createBuyTrade(CreateTradeRequest createTradeRequest, String username) {
        Long tradeId = tradeInitiationService.createBuyTrade(createTradeRequest, username);

        return tradeId;
    }

    @Transactional
    public Long acceptBuyRequest(ClaimBuyRequest claimBuyRequest, String username) {
        Long tradeId = tradeInitiationService.acceptBuyRequest(claimBuyRequest, username);

        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(tradeId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_TRADE_ROUTING_KEY, tradeMessageDto);

        return tradeId;
    }

    // === TradeProgressService === //
    @Transactional
    public void sendTradeData(Long tradeId, String username, MultipartFile picture) {
        Long sendTradeId = tradeProgressService.sendTradeData(tradeId, username);

        // 파일 업로드
        attachmentService.upload(tradeId, username, picture);

        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(sendTradeId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_TRADE_ROUTING_KEY, tradeMessageDto);
    }

    @Transactional
    public void confirmTrade(Long tradeId, String username) {
        Long confirmTradeId = tradeProgressService.confirmTrade(tradeId, username);

        TradeMessageDto tradeMessageDto = tradeMessageBuilder.buildTradeMessage(confirmTradeId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_TRADE_ROUTING_KEY, tradeMessageDto);
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

    // 거래 취소 요청
    @Transactional
    public void requestCancel(Long tradeId, String username, CancelReason reason) {
        tradeCancelService.requestCancel(tradeId, username, reason);
    }

    // 거래 취소 수락
    @Transactional
    public void acceptCancel(Long tradeId, String username) {
        tradeCancelService.acceptCancel(tradeId, username);
    }

    // 거래 취소 거절
    @Transactional
    public void rejectCancel(Long tradeId, String username) {
        tradeCancelService.rejectCancel(tradeId, username);
    }
}
