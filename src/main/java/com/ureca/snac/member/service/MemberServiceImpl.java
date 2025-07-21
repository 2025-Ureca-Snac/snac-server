package com.ureca.snac.member.service;

import com.ureca.snac.auth.dto.request.VerificationPhoneRequest;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.dto.request.EmailRequest;
import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import com.ureca.snac.member.dto.request.PhoneChangeRequest;
import com.ureca.snac.member.dto.request.PhoneRequest;
import com.ureca.snac.member.dto.response.EmailResponse;
import com.ureca.snac.member.exception.InvalidCurrentMemberInfoException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (!passwordEncoder.matches(req.getCurrentPassword(), member.getPassword())) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PASSWORD);
        }
        member.changePasswordTo(passwordEncoder.encode(req.getNewPassword()));
    }

    @Override
    @Transactional
    public void checkPhone(String email, String currentPhone) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (!member.getPhone().equals(currentPhone)) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PHONE);
        }
    }

    @Override
    public void changePhone(String email, String changePhone) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changePhoneTo(changePhone);
    }
}
