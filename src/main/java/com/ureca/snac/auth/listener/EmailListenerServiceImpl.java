package com.ureca.snac.auth.listener;

import com.ureca.snac.auth.exception.EmailSendFailedException;
import com.ureca.snac.auth.util.EmailTool;
import com.ureca.snac.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@Profile("!scheduler")
@RequiredArgsConstructor
public class EmailListenerServiceImpl implements EmailListenerService {

    private final StringRedisTemplate redisTemplate;
    private final EmailTool emailTool;

    private static final String VERIFICATION_CODE_PREFIX = "email:code:";
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(3);

    @Override
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
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

    private String generateRandomCode() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}
