package com.example.demo.util.jwt;

import com.example.demo.dto.KeyAndSerialNumberDto;

public interface JwtSecretKey {
    String getSignatureKey(String serialNumber);

    KeyAndSerialNumberDto getCurrentAccessTokenKey();

    KeyAndSerialNumberDto getCurrentRefreshTokenKey();
}
