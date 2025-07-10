package com.ureca.snac.auth.service;

import com.ureca.snac.auth.exception.SmsSendFailedException;
import com.ureca.snac.common.BaseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsServiceImpl implements SnsService{

    private final SnsClient snsClient;

    @Override
    public String sendVerificationCode(String phoneNumber) {
        String verificationCode = generateRandomCode();
        String message = String.format("[SNAC] 인증번호[%s]를 입력해주세요.", verificationCode);

        String formatPhoneNumber = formatToE164(phoneNumber);

        PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(formatPhoneNumber)
                .build();

        try {
            PublishResponse response = snsClient.publish(request);
            log.info("Sent message {} to {} with messageId {}", message, phoneNumber, response.messageId());
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", formatPhoneNumber, e.getMessage(), e);
            throw new SmsSendFailedException(BaseCode.SMS_SEND_FAILED);
        }
        return verificationCode;
    }

    // 6자리 숫자 랜덤으로.
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