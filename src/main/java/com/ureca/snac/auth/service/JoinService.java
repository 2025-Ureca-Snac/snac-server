package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.JoinDto;
import com.ureca.snac.auth.exception.BusinessException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.member.Activated.*;
import static com.ureca.snac.member.Role.*;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void joinProcess(JoinDto joinDto) {
        String email = joinDto.getEmail();

        if (authRepository.existsByEmail(email)) {
            throw new BusinessException(BaseCode.EMAIL_DUPLICATE);
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .name(joinDto.getName())
                .phone(joinDto.getPhone())
                .role(USER)
                .ratingScore(0)
                .activated(NORMAL)
                .build();

        authRepository.save(member);
    }
}
