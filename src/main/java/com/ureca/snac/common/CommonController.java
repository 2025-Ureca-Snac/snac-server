package com.ureca.snac.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
public class CommonController {

    @GetMapping("/api/health")
    public ResponseEntity<ApiResponse<?>> health() {
        return ResponseEntity
                .ok(ApiResponse.ok(STATUS_OK));
    }
}
