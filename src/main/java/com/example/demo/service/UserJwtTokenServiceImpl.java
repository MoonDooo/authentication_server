package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.OAuth2ResponseDto;
import com.example.demo.util.JwtParserForAuthentication;
import com.example.demo.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserJwtTokenServiceImpl implements UserJwtTokenService{
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public OAuth2ResponseDto createJWT(User user){
        Map<String, Object> info = new HashMap<>();
        List<String> roleList = user.getUserAuthorities().stream().map(auth -> auth.getRole().toString()).toList();
        info.put("role", roleList);
        String accessToken = jwtTokenProvider.createAccessToken(info, user.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(new HashMap<>(), user.getId().toString());
        return  OAuth2ResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValidityInMil())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValidityInMil()).build();
    }
}
