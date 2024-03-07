package com.example.demo.util.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BlackListJwtManagerImpl implements BlackListJwtManager {
    private final StringRedisTemplate stringRedisTemplate;

    private final String blackListJtiPrefix = "bl_" ;

    @Override
    public boolean isBlackListJwt(String jti){
        String b = stringRedisTemplate.opsForValue().get(blackListJtiPrefix + jti);
        return b != null && b.equals("true");
    }

    @Override
    public void addBlackListJwt(long expMillis, String jti){
        long nowMillis = System.currentTimeMillis();
        long diffSeconds = Math.max((expMillis - nowMillis / 1000), 1);
        stringRedisTemplate.opsForValue().set(blackListJtiPrefix+jti, "true", diffSeconds, TimeUnit.SECONDS);
    }
}
