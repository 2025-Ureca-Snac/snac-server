package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.*;

public interface MemberService {

    String findEmailByPhone(String phone);

    void changePassword(String email, PasswordChangeRequest request);

    void checkPassword(String email, PhoneChangeRequest request);

    void changePhone(String email, String changePhone);

    String changeNickname(String email, NicknameChangeRequest changeNickname);

    void resetPasswordByPhone(String phone, String newPwd);

    void resetPasswordByEmail(String email, String newPwd);
}
