package com.example.demo.util.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;

@RequiredArgsConstructor
@Component
public class JwtLocatorSecretKey implements Locator<Key> {
    private final JwtSecretKeyImpl jwtSecretKey;

    @Override
    public Key locate(Header header) {
        String kid = header.get("kid").toString();
        return getKey(jwtSecretKey.getSignatureKey(kid));
    }

    private static Key getKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
