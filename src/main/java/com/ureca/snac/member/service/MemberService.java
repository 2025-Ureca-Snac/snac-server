package com.ureca.snac.member.service;

import com.ureca.snac.auth.dto.request.VerificationPhoneRequest;
import com.ureca.snac.member.dto.request.EmailRequest;
import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import com.ureca.snac.member.dto.request.PhoneChangeRequest;
import com.ureca.snac.member.dto.request.PhoneRequest;
import com.ureca.snac.member.dto.response.EmailResponse;

public interface MemberService {

    EmailResponse findEmailByPhone(String phone);

    Boolean emailExist(EmailRequest emailRequest);

    void changePassword(String email, PasswordChangeRequest request);

    void checkPhone(String email, String currentPhone);

    void changePhone(String email, String changePhone);
}