package com.example.demo.oauth2.custom;


import com.example.demo.exception.StatusCode;
import com.example.demo.util.Check;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final RedisTemplate<String, OAuth2AuthorizationRequest> redisTemplate;



    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String stateParameter = request.getParameter(OAuth2ParameterNames.STATE);
        if (stateParameter == null){
            return null;
        }
        OAuth2AuthorizationRequest authorizationRequest = redisTemplate.opsForValue().get(stateParameter);
        Check.notNull(authorizationRequest, StatusCode.EXPIRED_LOGIN);
        return (authorizationRequest != null && stateParameter.equals(authorizationRequest.getState())) ? authorizationRequest : null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Check.notNull(request, StatusCode.AUTHENTICATION_FAILED, "request cannot be empty");
        Check.notNull(response, StatusCode.AUTHENTICATION_FAILED, "response cannot be null");
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }
        String state = authorizationRequest.getState();
        Check.notNull(state, StatusCode.AUTHENTICATION_FAILED, "authorizationRequest.state cannot be empty");
        redisTemplate.opsForValue().set(state,authorizationRequest,10, TimeUnit.HOURS);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {;
        OAuth2AuthorizationRequest authorizationRequest = redisTemplate.opsForValue().get(request.getParameter(OAuth2ParameterNames.STATE));
        redisTemplate.delete(request.getParameter(OAuth2ParameterNames.STATE));
        Check.notNull(authorizationRequest, StatusCode.EXPIRED_LOGIN);

        return authorizationRequest;
    }
}

