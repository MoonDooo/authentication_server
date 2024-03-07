package com.example.demo.oauth2;

import jakarta.servlet.http.HttpServletRequest;

public interface OAuth2LoginRegistrationIdResolver {
    String getRegistrationId(HttpServletRequest request);
}
