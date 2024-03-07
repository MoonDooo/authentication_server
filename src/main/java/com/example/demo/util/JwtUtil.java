package com.example.demo.util;

import com.example.demo.exception.StatusCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {
    public static String getToken(Map<String, Object> info, String subject, String keySerialNumberKey, String signatureKey, long validityTime, String issuer) {
        Check.notNull(signatureKey, StatusCode.Internal_Server_Error);
        Check.notNull(validityTime, StatusCode.Internal_Server_Error);
        Claims claims = Jwts.claims().subject(subject).add(info).build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityTime);

        Key accesstokenKey = getKey(signatureKey);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .header().keyId(keySerialNumberKey).add("type", "JWT").and()
                .expiration(validity)
                .signWith(accesstokenKey)
                .compact();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
