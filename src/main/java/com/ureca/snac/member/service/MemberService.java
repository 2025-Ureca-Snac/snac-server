package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.EmailRequest;
import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import com.ureca.snac.member.dto.request.PhoneRequest;
import com.ureca.snac.member.dto.response.EmailResponse;

public interface MemberService {

    EmailResponse findEmailByPhone(PhoneRequest phoneRequest);

    Boolean emailExist(EmailRequest emailRequest);

    void changePassword(String email, PasswordChangeRequest request);

}