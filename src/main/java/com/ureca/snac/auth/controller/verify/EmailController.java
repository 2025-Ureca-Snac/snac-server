package com.ureca.snac.auth.controller.verify;

import com.ureca.snac.auth.dto.request.EmailRequest;
import com.ureca.snac.auth.dto.request.VerificationEmailRequest;
import com.ureca.snac.auth.service.verify.EmailService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.config.RabbitMQConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class EmailController implements EmailControllerSwagger {

    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody EmailRequest dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, dto.getEmail());

//        emailService.sendVerificationCode(dto.getEmail()); // rabbitMQ로 비동기 처리 함
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_VERIFICATION_SENT));
    }



    @Override
    public ResponseEntity<ApiResponse<Void>> verifyCode(@Valid @RequestBody VerificationEmailRequest dto) {
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_CODE_VERIFICATION_SUCCESS));
    }
}
