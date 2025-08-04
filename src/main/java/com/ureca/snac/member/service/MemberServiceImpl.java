package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.NicknameChangeRequest;
import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import com.ureca.snac.member.dto.request.PhoneChangeRequest;
import com.ureca.snac.member.dto.response.CountMemberResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.InvalidCurrentPasswordException;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.exception.NicknameDuplicateException;
import com.ureca.snac.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            throw new InvalidCurrentPasswordException();
        }
        member.changePasswordTo(passwordEncoder.encode(req.getNewPwd()));
    }

    @Override
    public void checkPassword(String email, PhoneChangeRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        if (!passwordEncoder.matches(request.getPwd(), member.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }
    }

    @Override
    @Transactional
    public void changePhone(String email, String changePhone) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changePhoneTo(changePhone);
    }

    public void validateNicknameAvailable(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new NicknameDuplicateException();
        }
    }

    @Override
    @Transactional
    public String changeNickname(String email, NicknameChangeRequest nicknameChangeRequest) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changeNicknameTo(nicknameChangeRequest.getNickname());
        LocalDateTime unlockAt = member.getNextNicknameChangeAllowedAt();
        return unlockAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

    @Override
    public CountMemberResponse countMember() {
        return new CountMemberResponse(memberRepository.count());
    }

    @Override
    public void addRatingScore(Long memberId, int score) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        member.addRatingScore(score);
    }
}
