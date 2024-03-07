package com.example.demo.filter;

import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class CustomExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch(CustomException e){
            StatusCode statusCode = e.getStatusCode();
            responseStatusCode(response, statusCode);
        }catch(IllegalArgumentException e){
            log.info("exception illegal = {}", e.getMessage());
            responseStatusCode(response, StatusCode.AUTHENTICATION_FAILED);
        }
    }

    private static void responseStatusCode(HttpServletResponse response, StatusCode statusCode) throws IOException {
        log.debug("response : >");
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
