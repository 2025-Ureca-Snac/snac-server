package com.ureca.snac.auth.oauth2;

import com.ureca.snac.auth.exception.UnsupportedSocialProviderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    NAVER("naver"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String value;

    public static SocialProvider fromValue(String value) {
        for (SocialProvider socialProvider : values()) {
            if (socialProvider.value.equalsIgnoreCase(value)) {
                return socialProvider;
            }
        }
        throw new UnsupportedSocialProviderException();
    }
}