package com.ureca.snac.common.advice;

import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.trade.dto.SocketErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

import static com.ureca.snac.config.RabbitMQConfig.ERROR_EXCHANGE;
import static com.ureca.snac.config.RabbitMQConfig.ERROR_ROUTING_KEY;

@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final RabbitTemplate rabbitTemplate;

    @MessageExceptionHandler(BusinessException.class)
    public void handleCardAlreadyTradingException(BusinessException ex, Principal principal) {
        rabbitTemplate.convertAndSend(ERROR_EXCHANGE, ERROR_ROUTING_KEY, new SocketErrorDto(ex.getBaseCode(), principal.getName()));
    }
}
