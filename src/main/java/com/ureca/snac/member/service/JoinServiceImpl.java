package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.JoinRequest;
import com.ureca.snac.member.exception.EmailDuplicateException;
import com.ureca.snac.auth.exception.EmailNotVerifiedException;
import com.ureca.snac.member.exception.NicknameDuplicateException;
import com.ureca.snac.auth.exception.PhoneNotVerifiedException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.service.verify.EmailService;
import com.ureca.snac.auth.service.verify.SnsService;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.member.Activated.NORMAL;
import static com.ureca.snac.member.Role.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinServiceImpl implements JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SnsService snsService;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void joinProcess(JoinRequest joinRequest) {

        // 휴대폰 인증 여부 확인
        String phone = joinRequest.getPhone();
        if (!snsService.isPhoneVerified(phone)) {
            throw new PhoneNotVerifiedException();
        }
        log.info("휴대폰 < {} > 인증 되었음.", phone);

        String email = joinRequest.getEmail();

        // 이메일 인증 여부 확인
        if (!emailService.isEmailVerified(email)) {
            throw new EmailNotVerifiedException();
        }
        log.info("email < {} > 인증 되었음.", email);

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new EmailDuplicateException();
        }
        log.info("Email < {} > 은 중복이 아님.", email);

        String nickname = joinRequest.getNickname();
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new NicknameDuplicateException();
        }
        log.info("Nickname < {} > 은 중복이 아님.", nickname);

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .name(joinRequest.getName())
                .nickname(joinRequest.getNickname())
                .phone(phone)
                .birthDate(joinRequest.getBirthDate())
                .role(USER)
                .ratingScore(1000)
                .activated(NORMAL)
                .build();

        memberRepository.save(member);
        log.info("회원가입 완료됨! : 이메일 : {}, 이름 : {}", member.getEmail(),member.getName());

        // 여기서 이벤트 발행 서비스 동작
        publishMemberJoinEvent(member);
    }

    private void publishMemberJoinEvent(Member member) {
        eventPublisher.publishEvent(new MemberJoinEvent(member.getId())); // rabbitMQ 비동기 처리
    }
}
