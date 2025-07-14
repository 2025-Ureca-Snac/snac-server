package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReissueService {

    TokenDto reissue(String refresh);
}
