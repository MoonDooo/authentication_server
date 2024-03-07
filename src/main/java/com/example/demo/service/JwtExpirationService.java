package com.example.demo.service;

import java.util.Date;

public interface JwtExpirationService {
    void isIatOver(String sub, Date iat);

    void expTime(String sub, Date iat);
}
