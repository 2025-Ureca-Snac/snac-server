package com.ureca.snac.member.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.member.dto.response.EmailResponse;
import com.ureca.snac.member.exception.InvalidCurrentMemberInfoException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.exception.NicknameChangeTooEarlyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public EmailResponse findEmailByPhone(String phone) {
        Member member = memberRepository.findEmailByPhone(phone).orElseThrow(MemberNotFoundException::new);
        return EmailResponse.of(member.getEmail());
    }


    @Override
    public Boolean emailExist(EmailRequest emailRequest) {
        // 존재하면 true, 없으면 false
        return memberRepository.existsByEmail(emailRequest.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(String email, PasswordChangeRequest req) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(req.getCurrentPwd(), member.getPassword())) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PASSWORD);
        }
        member.changePasswordTo(passwordEncoder.encode(req.getNewPwd()));
    }
    @Override
    @Transactional
    public void checkPassword(String email, PhoneChangeRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        log.info("member.getPhone() : {}", member.getPhone());
        if (!passwordEncoder.matches(request.getPwd(), member.getPassword())) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PASSWORD);
        }
    }

    @Override
    public void changePhone(String email, String changePhone) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changePhoneTo(changePhone);
    }

    @Override
    public String changeNickname(String email, NicknameChangeRequest nicknameChangeRequest) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        if (ChronoUnit.HOURS.between(member.getNicknameUpdatedAt(), LocalDateTime.now()) < 24) {
            throw new NicknameChangeTooEarlyException();
        }
        member.changeNicknameTo(nicknameChangeRequest.getNickname());
        LocalDateTime nicknameUpdatedAt = member.getNicknameUpdatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return nicknameUpdatedAt.format(formatter);
    }


    /*    @Override
    @Transactional
    public void checkPhone(String email, String pwd) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (!member.getPhone().equals(pwd)) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PHONE);
        }
    }*/
}
