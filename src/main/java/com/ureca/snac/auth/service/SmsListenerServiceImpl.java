package com.ureca.snac.auth.service;

import com.ureca.snac.auth.exception.SmsSendFailedException;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.trade.dto.TradeMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsListenerServiceImpl implements SmsListenerService {

    private final SnsClient snsClient;

    @RabbitListener(queues = RabbitMQConfig.SMS_TRADE_QUEUE)
    public void sendSms(TradeMessageDto tradeMessageDto) {
        for (String phoneNumber : tradeMessageDto.getPhoneList()) {
            String formatPhoneNumber = formatToE164(phoneNumber);

            try {
                PublishResponse response = snsClient.publish(PublishRequest.builder()
                        .message(tradeMessageDto.getMessage())
                        .phoneNumber(formatPhoneNumber)
                        .build());
                log.info("RabbitMQ Sent message {} to {} with messageId {}", tradeMessageDto.getMessage(), phoneNumber, response.messageId());

                Thread.sleep(3000);

            } catch (Exception e) {
                log.error("Error sending SMS to {}: {}", formatPhoneNumber, e.getMessage(), e);
                throw new SmsSendFailedException();
            }
        }
    }

    private String formatToE164(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+82" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
