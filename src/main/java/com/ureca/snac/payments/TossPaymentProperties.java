package com.ureca.snac.payments;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties("payments.toss")
public class TossPaymentProperties {

    private final String secretKey;
    private final String confirmUrl;

    public TossPaymentProperties(String secretKey, String confirmUrl) {
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
    }

    public String getEncodedSecretKey() {
        return Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }
}