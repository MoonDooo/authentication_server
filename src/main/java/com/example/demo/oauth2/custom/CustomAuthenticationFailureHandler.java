package com.example.demo.oauth2.custom;

import com.example.demo.exception.StatusCode;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("message = {}", exception.getMessage());
        log.debug("AuthenticationEntryPoint : {}", (Object) exception.getStackTrace());
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
