package com.example.demo.util.jwt;

public interface BlackListJwtManager {
    boolean isBlackListJwt(String jti);

    void addBlackListJwt(long expMillis, String jti);
}
