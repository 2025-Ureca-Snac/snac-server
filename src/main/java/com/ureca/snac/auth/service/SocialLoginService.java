package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.TokenDto;

public interface SocialLoginService {
    TokenDto socialLogin(String socialToken);
}
