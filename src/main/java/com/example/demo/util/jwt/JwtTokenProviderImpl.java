package com.example.demo.util.jwt;

import com.example.demo.dto.KeyAndSerialNumberDto;
import com.example.demo.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;


@Getter
@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final JwtSecretKey jwtSecretKey;

    @Value("${security.jwt.access-token.validity-milliseconds}")
    private long accessTokenValidityInMil;

    @Value("${security.jwt.refresh-token.validity-milliseconds}")
    private long refreshTokenValidityInMil;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Override
    public String createAccessToken(Map<String, Object> info, String userId) {
        KeyAndSerialNumberDto dto = jwtSecretKey.getCurrentAccessTokenKey();
        return JwtUtil.getToken(info, userId, dto.getSerialNumber(), dto.getKey(), accessTokenValidityInMil, issuer);
    }

    @Override
    public String createRefreshToken(Map<String, Object> info, String userId) {
        KeyAndSerialNumberDto dto = jwtSecretKey.getCurrentRefreshTokenKey();
        return JwtUtil.getToken(info, userId, dto.getSerialNumber(), dto.getKey(), refreshTokenValidityInMil, issuer);
    }

    @Override
    public long getAccessTokenValidityInMil() {
        return accessTokenValidityInMil;
    }

    @Override
    public long getRefreshTokenValidityInMil() {
        return refreshTokenValidityInMil;
    }
}
