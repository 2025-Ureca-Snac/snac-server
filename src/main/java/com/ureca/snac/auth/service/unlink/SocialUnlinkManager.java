package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.exception.UnsupportedSocialProviderException;
import com.ureca.snac.auth.oauth2.SocialProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialUnlinkManager {

    private final Map<SocialProvider, SocialUnlinkService<?>> unlinkServices;

    public SocialUnlinkManager(List<SocialUnlinkService<?>> unlinkServices) {
        this.unlinkServices = unlinkServices.stream()
                .collect(Collectors.toUnmodifiableMap(
                        SocialUnlinkService::getProvider,
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked")
    public <T> T unlink(SocialProvider provider, String email) {
        SocialUnlinkService<T> service = (SocialUnlinkService<T>) getService(provider);
        return service.unlink(email);
    }

    private SocialUnlinkService<?> getService(SocialProvider provider) {
        SocialUnlinkService<?> service = unlinkServices.get(provider);
        if (service == null) {
            throw new UnsupportedSocialProviderException();
        }
        return service;
    }
}

