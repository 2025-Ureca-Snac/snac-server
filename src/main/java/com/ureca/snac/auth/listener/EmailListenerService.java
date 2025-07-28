package com.ureca.snac.auth.listener;

public interface EmailListenerService {

    void sendVerificationCode(String email);
}
