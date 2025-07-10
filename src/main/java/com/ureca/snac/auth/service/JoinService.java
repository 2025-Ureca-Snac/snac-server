package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.request.JoinRequest;

public interface JoinService {
    void joinProcess(JoinRequest joinRequest);
}