package com.example.demo.oauth2.custom;

import com.example.demo.exception.StatusCode;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("message = {}", authException.getMessage());
        log.debug("AuthenticationEntryPoint : {}", (Object) authException.getStackTrace());
        log.info("request = servletPath: {}, param-key: {}, param-value: {}, method: {}", request.getServletPath(), request.getParameterMap().keySet(), request.getParameterMap().values(), request.getMethod());


        StatusCode statusCode = StatusCode.AUTHENTICATION_FAILED;
        response.setStatus(statusCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", statusCode.getMessage());
        jsonObject.addProperty("code", statusCode.getCode());

        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }
}
