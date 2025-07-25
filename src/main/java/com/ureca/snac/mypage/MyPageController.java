package com.ureca.snac.mypage;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.mypage.dto.MyPageResponse;
import com.ureca.snac.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.MYPAGE_GET_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyPageController implements MyPageControllerSwagger {

    private final MyPageService myPageService;

    @Override
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPageInfo(CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        MyPageResponse response = myPageService.getMyPageInfo(email);
        return ResponseEntity.ok(ApiResponse.of(MYPAGE_GET_SUCCESS, response));
    }
}