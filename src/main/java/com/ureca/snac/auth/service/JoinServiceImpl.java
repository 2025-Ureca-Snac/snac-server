package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.request.JoinRequest;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.member.Activated.NORMAL;
import static com.ureca.snac.member.Role.USER;

@Service
@RequiredArgsConstructor
public class JoinServiceImpl implements JoinService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void joinProcess(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();

        if (authRepository.existsByEmail(email)) {
            throw new BusinessException(BaseCode.EMAIL_DUPLICATE);
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .name(joinRequest.getName())
                .phone(joinRequest.getPhone())
                .role(USER)
                .ratingScore(1000)
                .activated(NORMAL)
                .build();

        authRepository.save(member);
    }
}
