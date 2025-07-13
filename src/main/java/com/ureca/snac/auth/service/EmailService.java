package com.ureca.snac.auth.service;


public interface EmailService {

    void sendVerificationCode(String email);

    void verifyCode(String email, String code);

    boolean isEmailVerified(String email);
}