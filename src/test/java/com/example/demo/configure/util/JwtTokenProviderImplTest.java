package com.example.demo.configure.util;

import com.example.demo.util.jwt.JwtTokenProviderImpl;
import com.example.demo.util.jwt.JwtLocatorSecretKey;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class JwtTokenProviderImplTest {
    private final JwtTokenProviderImpl createJwtToken;
    private final JwtLocatorSecretKey jwtLocatorSecretKey;
    @Autowired
    JwtTokenProviderImplTest(JwtTokenProviderImpl createJwtToken, JwtLocatorSecretKey jwtLocatorSecretKey) {
        this.createJwtToken = createJwtToken;
        this.jwtLocatorSecretKey = jwtLocatorSecretKey;
    }

    @Test
    void createAccessToken() {
        Map<String,Object> map = new HashMap<>();
        map.put("test","test");
        String accessToken = createJwtToken.createAccessToken(map, String.valueOf(1));

        Map<String,String> payload = (Map<String,String>) Jwts.parser().keyLocator(jwtLocatorSecretKey).build().parse(accessToken).getPayload();

        Assertions.assertThat(payload.get("test")).isEqualTo("test");
    }

    @Test
    void createRefreshToken() {
        Map<String,Object> map = new HashMap<>();
        map.put("test","test");
        String accessToken = createJwtToken.createRefreshToken(map, String.valueOf(1));

        Map<String,String> payload = (Map<String,String>) Jwts.parser().keyLocator(jwtLocatorSecretKey).build().parse(accessToken).getPayload();

        Assertions.assertThat(payload.get("test")).isEqualTo("test");
    }
}