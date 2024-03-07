package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.OAuth2ResponseDto;

public interface UserJwtTokenService {
    OAuth2ResponseDto createJWT(User user);
}
