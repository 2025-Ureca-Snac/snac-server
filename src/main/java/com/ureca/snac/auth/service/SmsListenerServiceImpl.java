package com.ureca.snac.auth.service;

import com.ureca.snac.auth.exception.SmsSendFailedException;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.trade.dto.TradeMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsListenerServiceImpl implements SmsListenerService {

    private final SnsClient snsClient;
    private final StringRedisTemplate redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "sms:code:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(3);

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

    @RabbitListener(queues = RabbitMQConfig.SMS_AUTH_QUEUE)
    public void sendVerificationCode(String phoneNumber) {
        String verificationCode = generateRandomCode();
        String message = String.format("[SNAC] 인증번호[%s]를 입력해주세요.", verificationCode);
        String formatPhoneNumber = formatToE164(phoneNumber);

        try {
            PublishResponse response = snsClient.publish(PublishRequest.builder()
                    .message(message)
                    .phoneNumber(formatPhoneNumber)
                    .build());

            redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + phoneNumber, verificationCode, VERIFICATION_CODE_TTL);

            log.info("Sent message {} to {} with messageId {}", message, phoneNumber, response.messageId());
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", formatPhoneNumber, e.getMessage(), e);
            throw new SmsSendFailedException();
        }
    }

    private String generateRandomCode() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    private String formatToE164(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+82" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
