package com.ureca.snac.member.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.dto.request.*;
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
    public String findEmailByPhone(String phone) {
        return memberRepository.findEmailByPhone(phone).orElseThrow(MemberNotFoundException::new);
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
    public void checkPassword(String email, PhoneChangeRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        if (!passwordEncoder.matches(request.getPwd(), member.getPassword())) {
            throw new InvalidCurrentMemberInfoException(BaseCode.INVALID_CURRENT_PASSWORD);
        }
    }

    @Override
    @Transactional
    public void changePhone(String email, String changePhone) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changePhoneTo(changePhone);
    }

    @Override
    @Transactional
    public String changeNickname(String email, NicknameChangeRequest nicknameChangeRequest) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        LocalDateTime lastUpdated = member.getNicknameUpdatedAt();
        if (lastUpdated!=null && ChronoUnit.HOURS.between(lastUpdated, LocalDateTime.now()) < 24) {
            throw new NicknameChangeTooEarlyException();
        }
        member.changeNicknameTo(nicknameChangeRequest.getNickname());
        return member.getNicknameUpdatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    @Override
    @Transactional
    public void resetPasswordByPhone(String phone, String newPwd) {
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(MemberNotFoundException::new);

        member.changePasswordTo(passwordEncoder.encode(newPwd));
    }

    @Override
    @Transactional
    public void resetPasswordByEmail(String email, String newPwd) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        member.changePasswordTo(passwordEncoder.encode(newPwd));
    }
}
