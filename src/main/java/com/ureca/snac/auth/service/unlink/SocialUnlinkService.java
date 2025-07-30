package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.oauth2.SocialProvider;

public interface SocialUnlinkService<T> {
    SocialProvider getProvider();
    T unlink(String email);
}
