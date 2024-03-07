package com.example.demo.filter;

import com.example.demo.dto.OAuth2ResponseDto;
import com.example.demo.exception.StatusCode;
import com.example.demo.service.JwtExpirationService;
import com.example.demo.service.JwtRefreshService;
import com.example.demo.util.Check;
import com.example.demo.util.JwtParserForAuthentication;
import com.example.demo.util.jwt.BlackListJwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtRefreshFilter extends OncePerRequestFilter {

    private final JwtRefreshService jwtRefreshService;

    private final JwtParserForAuthentication jwtParserForAuthentication;
    private final JwtExpirationService jwtExpirationService;

    @Value("${security.jwt.header}")
    private String jwtHeader;
    private final String refreshParam = "refresh";
    private RequestMatcher refreshJwtMatcher;

    @Autowired
    public JwtRefreshFilter(BlackListJwtManager blackListJwtManager, JwtParserForAuthentication jwtParserForAuthentication, JwtRefreshService jwtRefreshService, JwtParserForAuthentication jwtParserForAuthentication1, JwtExpirationService jwtExpirationService) {
        this.jwtRefreshService = jwtRefreshService;
        this.jwtParserForAuthentication = jwtParserForAuthentication1;
        this.jwtExpirationService = jwtExpirationService;
        this.refreshJwtMatcher = request -> {
            boolean uriMatch = new AntPathMatcher().match("/token", request.getRequestURI());
            boolean isPut = request.getMethod().equals(HttpMethod.PUT.name());
            return uriMatch&&isPut;
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (refreshJwtMatcher.matches(request)){
            String refreshToken = request.getParameter(refreshParam);
            Claims claims = jwtParserForAuthentication.getClaims(refreshToken);
            Check.notNull(claims, StatusCode.EXPIRED_TOKEN);
            Check.notNull(claims.getSubject(), StatusCode.NOT_VALID_TOKEN);
            OAuth2ResponseDto refresh = jwtRefreshService.refresh(Long.valueOf(claims.getSubject()));
            jwtExpirationService.expTime(claims.getSubject(), new Date());
            response.setContentType(ContentType.APPLICATION_JSON.getType());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(refresh));
        }else{
            filterChain.doFilter(request, response);
        }
    }
}
