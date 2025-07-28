package com.ureca.snac.auth.controller.verify;

import com.ureca.snac.auth.dto.request.PhoneRequest;
import com.ureca.snac.auth.dto.request.VerificationPhoneRequest;
import com.ureca.snac.auth.service.verify.SnsService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class SnsController implements SnsControllerSwagger {

    private final SnsService snsService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody PhoneRequest dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_AUTH_ROUTING_KEY, dto.getPhone());
//        snsService.sendVerificationCode(dto.getPhone()); // 기존 코드를 rabbitMQ 비동기로 변경했습니다.
        return ResponseEntity.ok(ApiResponse.ok(SMS_VERIFICATION_SENT));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody VerificationPhoneRequest dto) {
        snsService.verifyCode(dto.getPhone(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.ok(SMS_CODE_VERIFICATION_SUCCESS));
    }
}
