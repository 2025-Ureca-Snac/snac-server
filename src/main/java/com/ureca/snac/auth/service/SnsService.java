package com.ureca.snac.auth.service;

public interface SnsService {

    void sendVerificationCode(String phoneNumber);

    void verifyCode(String phoneNumber, String code);

    boolean isPhoneVerified(String phoneNumber);

    void sendSms(String phoneNumber, String message);
}
