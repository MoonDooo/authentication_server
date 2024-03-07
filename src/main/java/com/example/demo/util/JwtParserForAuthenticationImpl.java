package com.example.demo.util;

import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.service.JwtExpirationService;
import com.example.demo.util.jwt.BlackListJwtManager;
import com.example.demo.util.jwt.JwtLocatorSecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtParserForAuthenticationImpl implements JwtParserForAuthentication{
    private final JwtLocatorSecretKey jwtLocatorSecretKey;
    private final BlackListJwtManager blackListJwtManager;
    private final JwtExpirationService jwtExpirationService;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Override
    public Claims getClaims(String jwt){
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser()
                    .keyLocator(jwtLocatorSecretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(jwt);
        }catch(ExpiredJwtException e){
            log.debug("{} 는 만됴되었습니다.", jwt);
            throw new CustomException(StatusCode.EXPIRED_TOKEN);
        }catch(Exception e){
            log.debug("",e);
            throw new CustomException(StatusCode.NOT_VALID_TOKEN);
        }
        Claims payload = getPayload(claimsJws);
        checkBlackList(payload);
        jwtExpirationService.isIatOver(payload.getSubject(), payload.getIssuedAt());
        return payload;

    }

    private static Claims getPayload(Jws<Claims> claimsJws) {
        Check.notNull(claimsJws, StatusCode.NOT_VALID_TOKEN);
        Claims payload = claimsJws.getPayload();
        Check.notNull(payload.get("sub"), StatusCode.NOT_VALID_TOKEN);
        Check.notNull(payload.get("jti"), StatusCode.NOT_VALID_TOKEN);
        Check.notNull(payload.get("iat"), StatusCode.NOT_VALID_TOKEN);
        return payload;
    }

    private void checkBlackList(Claims payload) {
        if (blackListJwtManager.isBlackListJwt(String.valueOf(payload.get("jti")))){
            throw new CustomException(StatusCode.NOT_VALID_TOKEN);
        }
    }
}
