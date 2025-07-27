package com.ureca.snac.mypage.service;

import com.ureca.snac.mypage.dto.MyPageResponse;

public interface MyPageService {
    MyPageResponse getMyPageInfo(String email);
}
