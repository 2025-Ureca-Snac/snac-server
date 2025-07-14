package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member userData = authRepository.findByEmail(email);

        if (userData == null) {
            throw new UsernameNotFoundException("사용자를 찾지 못했어요.. email: " + email);
        }

        return new CustomUserDetails(userData);
    }
}
