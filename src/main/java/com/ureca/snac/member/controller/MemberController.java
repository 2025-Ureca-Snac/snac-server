package com.ureca.snac.member.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.dto.request.PhoneRequest;
import com.ureca.snac.auth.service.verify.EmailService;
import com.ureca.snac.auth.service.verify.SnsService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.member.dto.response.CountMemberResponse;
import com.ureca.snac.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;
    private final SnsService snsService;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;

    //TODO 비밀번호 변경(완료, 검증완료)
    // 사용자 인증 추가 해야 할듯? -> 안하기로 함. 로그아웃 시켜버리는 방식으로

    // 1. 비밀번호 현재 비밀번호와 함께 입력하여 변경
    @Override
    public ResponseEntity<ApiResponse<Void>> changePwd(
            CustomUserDetails userDetails,
            @Valid PasswordChangeRequest request
    ) {
        memberService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PASSWORD_CHANGED));
    }

    // 2. 로그아웃 시키기
    // /api/logout


    //TODO 전화번호 변경(완료, 검증완료)

    // 1. 비밀번호 맞는지 확인 하고, 인증번호 요청
    @Override
    public ResponseEntity<ApiResponse<Void>> checkPhone(
            CustomUserDetails userDetails,
            @Valid PhoneChangeRequest phoneChangeRequest) {

        memberService.checkPassword(userDetails.getUsername(), phoneChangeRequest);

        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_AUTH_ROUTING_KEY, phoneChangeRequest.getNewPhone());
//        snsService.sendVerificationCode(phoneChangeRequest.getNewPhone()); // 기존 코드를 rabbitMQ 비동기로 변경했습니다.
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PHONE_EXIST_SUCCESS));
    }

    // 2. 인증 번호 맞는지 확인
    // /api/sns/verify-code

    // 3. 맞으면 교체
    @Override
    public ResponseEntity<ApiResponse<Void>> changePhone(
            CustomUserDetails userDetails,
            @Valid PhoneRequest phoneRequest
    ) {
        String changePhone = phoneRequest.getPhone();
        boolean phoneVerified = snsService.isPhoneVerified(changePhone);
        if (!phoneVerified) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.PHONE_NOT_VERIFIED));
        }
        memberService.changePhone(userDetails.getUsername(), changePhone);
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PHONE_CHANGED));
    }

    //TODO 닉네임 변경 가능 여부(완료, 검증완료)
    @Override
    public ResponseEntity<ApiResponse<Void>> checkNicknameDuplicate(@Valid NicknameChangeRequest request) {
        memberService.validateNicknameAvailable(request.getNickname());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.NICKNAME_AVAILABLE));
    }


    //TODO 닉네임 변경(완료, 검증완료)

    @Override
    public ResponseEntity<ApiResponse<String>> changeNickname(
            CustomUserDetails userDetails,
            @Valid NicknameChangeRequest nicknameChangeRequest) {
        String unlockAt = memberService.changeNickname(userDetails.getUsername(), nicknameChangeRequest);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.NICKNAME_CHANGED, unlockAt));
    }

    //TODO 휴대폰 인증으로 이메일 찾기 (완료, 검증완료)

    // 1. 이메일 찾기 시도하면 휴대폰 인증 시키기
    // /api/sns/send-verification-code

    // 2. 검증
    // /api/sns/verify-code

    // 3. 검증 되었는지 확인 후 이메일 찾아주기
    @Override
    public ResponseEntity<ApiResponse<String>> findEmail(@Valid PhoneRequest phoneRequest) {
        String phone = phoneRequest.getPhone();
        boolean phoneVerified = snsService.isPhoneVerified(phone);
        if (!phoneVerified) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.PHONE_NOT_VERIFIED));
        }
        String emailByPhone = memberService.findEmailByPhone(phone);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.EMAIL_FOUND_BY_PHONE, emailByPhone));
    }


    //TODO
    // 비밀번호 찾기(재생성) (휴대폰 번호 또는 이메일) (완료, 검증완료)

    // 휴대폰 번호

    // 1. 휴대폰 인증을 받는다
    // /api/sns/send-verification-code

    // 2. 검증
    // /api/sns/verify-code

    // 3. 비밀번호 교체
    @Override
    public ResponseEntity<ApiResponse<Void>> resetPwdByPhone(
            @Valid PasswordResetByPhoneRequest req) {

        String phone = req.getPhone();
        if (!snsService.isPhoneVerified(phone)) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.PHONE_NOT_VERIFIED));
        }
        memberService.resetPasswordByPhone(phone, req.getNewPwd());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PASSWORD_CHANGED));
    }

    // 이메일

    // 1. 이메일 인증을 받는다
    // /api/email/send-verification-code

    // 2. 검증
    // /api/email/verify-code

    // 3. 비밀번호 교체
    @Override
    public ResponseEntity<ApiResponse<Void>> resetPwdByEmail(
            @Valid PasswordResetByEmailRequest req) {

        String email = req.getEmail();
        if (!emailService.isEmailVerified(email)) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.EMAIL_NOT_VERIFIED));
        }
        memberService.resetPasswordByEmail(email, req.getNewPwd());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PASSWORD_CHANGED));
    }

    @Override
    public ResponseEntity<ApiResponse<CountMemberResponse>> countMembers(@AuthenticationPrincipal UserDetails userDetails) {
        CountMemberResponse count = memberService.countMember();
        return ResponseEntity.ok(ApiResponse.of(BaseCode.MEMBER_COUNT_SUCCESS, count));
    }
}

