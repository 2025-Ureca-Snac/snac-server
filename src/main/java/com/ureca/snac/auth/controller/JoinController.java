package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.JoinDto;
import com.ureca.snac.auth.service.JoinService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/api/join")
    public ApiResponse<Void> joinProcess(@RequestBody JoinDto joinDto) {

        joinService.joinProcess(joinDto);
        return ApiResponse.ok(BaseCode.USER_SIGNUP_SUCCESS);
    }
}