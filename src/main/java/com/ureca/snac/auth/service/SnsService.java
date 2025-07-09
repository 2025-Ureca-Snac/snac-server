package com.ureca.snac.auth.service;

public interface SnsService {

    String sendVerificationCode(String phoneNumber);
}
