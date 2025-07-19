package com.ureca.snac.auth.service;

import java.util.List;

public interface SnsService {

    void sendVerificationCode(String phoneNumber);

    void verifyCode(String phoneNumber, String code);

    boolean isPhoneVerified(String phoneNumber);

    void sendSms(List<String> phoneNumberList, String message);
}
