package com.ureca.snac.auth.dto;

import com.ureca.snac.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@RequiredArgsConstructor
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Member member;
    private final String provider;
    private final String providerId;
    private final Map<String,Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> member.getRole().name());

        return collection;
    }

    @Override
    public String getName() {

        return member.getName();
    }

    public String getEmail() {
        return member.getEmail();
    }
}