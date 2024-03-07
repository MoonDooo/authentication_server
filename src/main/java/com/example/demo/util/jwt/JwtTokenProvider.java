package com.example.demo.util.jwt;

import java.util.Map;

public interface JwtTokenProvider {
    String createAccessToken(Map<String, Object> info, String userId);

    String createRefreshToken(Map<String, Object> info, String userId);

    long getAccessTokenValidityInMil();

    long getRefreshTokenValidityInMil();
}
