package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.JoinRequest;
import com.ureca.snac.auth.service.JoinServiceImpl;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class JoinController implements JoinControllerSwagger {

    private final JoinServiceImpl joinServiceImpl;

    @Override
    public ResponseEntity<ApiResponse<Void>> joinProcess(@RequestBody JoinRequest joinRequest) {
        joinServiceImpl.joinProcess(joinRequest);
        return ResponseEntity.ok(ApiResponse.ok(USER_SIGNUP_SUCCESS));
    }
}