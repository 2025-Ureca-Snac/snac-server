package com.ureca.snac.finance.service;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = false)
public class AccountNumberConverter implements AttributeConverter<String, String> {

    private final TextEncryptor encryptor;

    public AccountNumberConverter(@Value("${aes.passphrase}") String passphrase,
                                  @Value("${aes.salt}")      String hexSalt) {
        this.encryptor = Encryptors.delux(passphrase, hexSalt);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return encryptor.decrypt(dbData);
    }
}
