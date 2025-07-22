package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.member.dto.response.EmailResponse;

public interface MemberService {

    EmailResponse findEmailByPhone(String phone);

    Boolean emailExist(EmailRequest emailRequest);

    void changePassword(String email, PasswordChangeRequest request);

    void checkPassword(String email, PhoneChangeRequest request);

    void changePhone(String email, String changePhone);

    String changeNickname(String email, NicknameChangeRequest changeNickname);
}
