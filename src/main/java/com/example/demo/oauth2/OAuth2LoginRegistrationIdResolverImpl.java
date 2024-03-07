package com.example.demo.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginRegistrationIdResolverImpl implements OAuth2LoginRegistrationIdResolver{
    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final AntPathRequestMatcher antPathRequestMatcher;
    private String requestLoginBaseUri = "/login/oauth2/code/";

    public OAuth2LoginRegistrationIdResolverImpl() {
        this.antPathRequestMatcher = new AntPathRequestMatcher(requestLoginBaseUri + "{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    @Override
    public String getRegistrationId(HttpServletRequest request) {
        if (antPathRequestMatcher.matches(request)){
            return antPathRequestMatcher.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }
}
