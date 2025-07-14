package com.ureca.snac.auth.service;

import com.ureca.snac.auth.exception.EmailSendFailedException;
import com.ureca.snac.auth.exception.VerificationFailedException;
import com.ureca.snac.auth.util.EmailTool;
import com.ureca.snac.common.BaseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class EmailServiceImpl implements EmailService {

    private final StringRedisTemplate redisTemplate;
    private final EmailTool emailTool;

    private static final String VERIFICATION_CODE_PREFIX = "email:code:";
    private static final String VERIFIED_FLAG_PREFIX = "email:verified:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(3);
    private static final Duration VERIFIED_FLAG_TTL = Duration.ofMinutes(10);

    @Override
    public void sendVerificationCode(String email) {
        String verificationCode = generateRandomCode();
        String title = "[SNAC] 이메일 인증 코드";
        String message = String.format("""
            안녕하세요, SNAC입니다.
            요청하신 이메일 인증번호는 다음과 같습니다.
            
            인증번호: %s
            
            SNAC 앱으로 돌아가 화면에 이 인증번호를 입력해주세요.
            인증번호는 5분간 유효합니다.
            
            본인이 요청하지 않으셨다면 이 메일을 무시하셔도 됩니다.
            
            감사합니다.
            SNAC 팀 드림
            """, verificationCode);

        try {
            emailTool.sendEmail(email, title, message);
            // 이메일 전송 성공 했을때 인증 코드를 Redis에 저장
            redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + email, verificationCode, VERIFICATION_CODE_TTL);
            log.info("Sent verification email to {}", email);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", email, e.getMessage(), e);
            throw new EmailSendFailedException();
        }
    }

    @Override
    public void verifyCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        // 1. 저장된 코드가 없는 경우
        if (storedCode == null) {
            throw new VerificationFailedException(BaseCode.EMAIL_CODE_VERIFICATION_EXPIRED);
        }

        // 2. 입력된 코드와 저장된 코드가 안맞는 경우
        if (!storedCode.equals(code)) {
            throw new VerificationFailedException(BaseCode.EMAIL_CODE_VERIFICATION_MISMATCH);
        }

        // 3. 검증 성공
        redisTemplate.delete(key);
        log.info("Email < {} > 인증코드 메모리 삭제되었음", email);

        redisTemplate.opsForValue().set(VERIFIED_FLAG_PREFIX + email, "true", VERIFIED_FLAG_TTL);
        log.info("Email < {} > 검증 완료되었음", email);
    }

    @Override
    public boolean isEmailVerified(String email) {
        String flag = redisTemplate.opsForValue().get(VERIFIED_FLAG_PREFIX + email);
        return "true".equals(flag);
    }

    private String generateRandomCode() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}

