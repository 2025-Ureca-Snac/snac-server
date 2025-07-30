package com.ureca.snac.member.service;

import com.ureca.snac.member.dto.request.JoinRequest;

public interface JoinService {
    void joinProcess(JoinRequest joinRequest);
}