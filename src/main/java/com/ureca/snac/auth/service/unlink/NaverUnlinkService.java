package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.dto.response.NaverUnlinkResponse;

public interface NaverUnlinkService {
    NaverUnlinkResponse unlinkNaverUser(String email);
}
