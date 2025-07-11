package com.ureca.snac.auth.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 86400)
public class Refresh {

    @Id
    private String email;

    @Indexed
    private String refresh;
}