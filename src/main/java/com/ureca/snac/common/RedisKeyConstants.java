package com.ureca.snac.common;

public final class RedisKeyConstants {
    private RedisKeyConstants() {}

    // 현재 접속중인 사용자 셋 키
    public static final String CONNECTED_USERS = "connected_users";

    // 구매자 필터 저장용 키 prefix (“buyer_filter:{username}”)
    public static final String BUYER_FILTER_PREFIX = "buyer_filter:";
    public static final String WS_DISCONNECT_LOCK_PREFIX  = "lock:ws:disconnect:";
    public static final String REDISSON_HOST_PREFIX = "redis://";
}
