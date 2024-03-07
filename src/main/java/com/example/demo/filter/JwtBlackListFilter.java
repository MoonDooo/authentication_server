package com.example.demo.filter;

import com.example.demo.dto.JwtTokenDetails;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.service.JwtExpirationService;
import com.example.demo.util.Check;
import com.example.demo.util.jwt.BlackListJwtManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtBlackListFilter extends OncePerRequestFilter {
    @Setter
    private RequestMatcher blackJwtMatcher;
    private final BlackListJwtManager blackListJwtManager;
    private final JwtExpirationService jwtExpirationService;
    @Value("${security.jwt.header}")
    private String jwtHeader;

    @Autowired
    public JwtBlackListFilter(BlackListJwtManager blackListJwtManager, JwtExpirationService jwtExpirationService) {
        this.blackListJwtManager = blackListJwtManager;
        this.jwtExpirationService = jwtExpirationService;
        this.blackJwtMatcher = request -> {
            boolean uriMatch = new AntPathMatcher().match("/token", request.getRequestURI());
            boolean isDelete = request.getMethod().equals(HttpMethod.DELETE.name());
            return uriMatch&&isDelete;
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (blackJwtMatcher.matches(request)){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!auth.isAuthenticated()){
                throw new CustomException(StatusCode.AUTHENTICATION_FAILED);
            }
            JwtTokenDetails details = (JwtTokenDetails) auth.getDetails();
            Check.notNull(details, StatusCode.Internal_Server_Error);
            blackListJwtManager.addBlackListJwt(details.getExp().getTime(), details.getId());
            jwtExpirationService.expTime(auth.getName(), new Date());
        }else{
            filterChain.doFilter(request,response);
        }
    }
}
