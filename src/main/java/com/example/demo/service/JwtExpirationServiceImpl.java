package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import io.jsonwebtoken.Jwts;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service

public class JwtExpirationServiceImpl implements JwtExpirationService {
    private final RedisTemplate<String,Date> redisTemplate;

    public JwtExpirationServiceImpl(RedisTemplate<String, Date> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void isIatOver(String sub, Date iat){
        Date lastIat = redisTemplate.opsForValue().get(sub);
        if (lastIat == null) return;

        // jwt의 iat은 초 단위이므로
        Date lastIatMinusOneSecond = new Date(lastIat.getTime() - 1000);

        if (iat.before(lastIatMinusOneSecond)) {
            throw new CustomException(StatusCode.EXPIRED_TOKEN);
        }
    }

    @Override
    public void expTime(String sub, Date iat){
        redisTemplate.opsForValue().set(sub, iat);
    }

}
