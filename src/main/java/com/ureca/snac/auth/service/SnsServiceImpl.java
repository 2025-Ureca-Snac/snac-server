package com.ureca.snac.auth.service;

import com.ureca.snac.auth.exception.SmsSendFailedException;
import com.ureca.snac.auth.exception.VerificationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.Duration;
import java.util.Random;

import static com.ureca.snac.common.BaseCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsServiceImpl implements SnsService {

    private final SnsClient snsClient;
    private final StringRedisTemplate redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "sms:code:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(3);

    @Override
    public void sendVerificationCode(String phoneNumber) {
        String verificationCode = generateRandomCode();
        String message = String.format("[SNAC] 인증번호[%s]를 입력해주세요.", verificationCode);
        String formatPhoneNumber = formatToE164(phoneNumber);

        PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(formatPhoneNumber)
                .build();

        try {
            PublishResponse response = snsClient.publish(request);
            redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + phoneNumber, verificationCode, VERIFICATION_CODE_TTL);
            log.info("Sent message {} to {} with messageId {}", message, phoneNumber, response.messageId());
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", formatPhoneNumber, e.getMessage(), e);
            throw new SmsSendFailedException(SMS_SEND_FAILED);
        }
    }

    @Override
    public void verifyCode(String phoneNumber, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + phoneNumber);

        // 저장된 코드가 없는 경우 만료 or 요청x
        if (storedCode == null) {
            throw new VerificationFailedException(SMS_CODE_VERIFICATION_EXPIRED);
        }

        // 입력코드랑 저장된 코드가 일치하지 않는 경우
        if (!storedCode.equals(code)) {
            throw new VerificationFailedException(SMS_CODE_VERIFICATION_MISMATCH);
        }

        // 검증 성공 시 Redis에서 코드 삭제
        redisTemplate.delete(VERIFICATION_CODE_PREFIX + phoneNumber);
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