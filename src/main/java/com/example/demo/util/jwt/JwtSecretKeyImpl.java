package com.example.demo.util.jwt;


import com.example.demo.dto.KeyAndSerialNumberDto;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtSecretKeyImpl implements JwtSecretKey {
    private String currentAccessTokenSignatureKey;
    private String currentAccessTokenSerialNumber;
    private String currentRefreshTokenSignatureKey;
    private String currentRefreshTokenSerialNumber;


    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public JwtSecretKeyImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String getSignatureKey(String serialNumber){
        return stringRedisTemplate.opsForValue().get(serialNumber);
    }

    @Override
    public KeyAndSerialNumberDto getCurrentAccessTokenKey(){
        synchronized (this){
            return KeyAndSerialNumberDto
                    .builder()
                    .key(currentAccessTokenSignatureKey)
                    .serialNumber(currentAccessTokenSerialNumber)
                    .build();
        }
    }

    @Override
    public KeyAndSerialNumberDto getCurrentRefreshTokenKey(){
        synchronized (this){
            return KeyAndSerialNumberDto
                    .builder()
                    .key(currentRefreshTokenSignatureKey)
                    .serialNumber(currentRefreshTokenSerialNumber)
                    .build();
        }
    }


    /**
     * access token
     */
    private void setAccessTokenSignatureKey(String secretKey){
        currentAccessTokenSignatureKey = secretKey;
        currentAccessTokenSerialNumber = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(currentAccessTokenSerialNumber, currentAccessTokenSignatureKey, 20, TimeUnit.HOURS);
    }

    /**
     * refresh token
     */
    private void setRefreshTokenSignatureKey(String secretKey){
        currentRefreshTokenSignatureKey = secretKey;
        currentRefreshTokenSerialNumber = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(currentRefreshTokenSerialNumber, currentRefreshTokenSignatureKey, 14, TimeUnit.DAYS);
    }

    @Scheduled(fixedDelay = 7200000)
    private void createAccessSignatureKey(){
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            setAccessTokenSignatureKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(StatusCode.Internal_Server_Error);
        }
    }

    @Scheduled(fixedDelay = 86400000)
    private void createRefreshSignatureKey(){
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            setRefreshTokenSignatureKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(StatusCode.Internal_Server_Error);
        }
    }

}
