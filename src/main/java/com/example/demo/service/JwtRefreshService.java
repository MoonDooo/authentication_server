package com.example.demo.service;

import com.example.demo.dto.OAuth2ResponseDto;

public interface JwtRefreshService {
    OAuth2ResponseDto refresh(Long sub);
}
