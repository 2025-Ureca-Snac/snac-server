package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/user/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok("@AuthenticationPrincipal 테스트, " + user.getUsername());
    }
}