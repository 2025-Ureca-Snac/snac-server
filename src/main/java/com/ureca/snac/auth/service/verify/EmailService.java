package com.ureca.snac.auth.service.verify;


public interface EmailService {

    void sendVerificationCode(String email);

    void verifyCode(String email, String code);

    boolean isEmailVerified(String email);
}