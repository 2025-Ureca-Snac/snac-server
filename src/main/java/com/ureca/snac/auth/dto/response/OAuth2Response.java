package com.ureca.snac.auth.dto.response;

public interface OAuth2Response {

    //제공자 ( ex) naver, google, kakao )
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
}
