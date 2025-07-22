package com.ureca.snac.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.stomp")
public class StompRelayProperties {
    private String host;
    private int port;
    private String clientLogin;
    private String clientPasscode;
    private String systemLogin;
    private String systemPasscode;
}
