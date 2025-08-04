package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.member.dto.response.CountMemberResponse;

public interface MemberService {

    String findEmailByPhone(String phone);

    void changePassword(String email, PasswordChangeRequest request);

    void checkPassword(String email, PhoneChangeRequest request);

    void changePhone(String email, String changePhone);

    void validateNicknameAvailable(String nickname);

    String changeNickname(String email, NicknameChangeRequest changeNickname);

    void resetPasswordByPhone(String phone, String newPwd);

    void resetPasswordByEmail(String email, String newPwd);

    CountMemberResponse countMember();

    void addRatingScore(Long memberId, int score);
}
