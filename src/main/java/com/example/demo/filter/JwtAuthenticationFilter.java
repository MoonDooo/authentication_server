package com.example.demo.filter;

import com.example.demo.dto.JwtTokenDetails;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.util.JwtParserForAuthentication;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtParserForAuthentication jwtParserForAuthentication;

    @Value("${security.jwt.header}")
    private String jwtHeader;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(jwtHeader);
        if (jwt == null){
            filterChain.doFilter(request, response);
            return;
        }
        Claims payload = jwtParserForAuthentication.getClaims(jwt);
        String sub = String.valueOf(payload.get("sub"));
        List<String> role = null;
        if (payload.get("role") instanceof List){
            role = (List<String>) payload.get("role");
        }else{
            throw new CustomException(StatusCode.NOT_VALID_TOKEN);
        }
        List<GrantedAuthority> grant = role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sub, null, grant);
        authenticationToken.setDetails(new JwtTokenDetails(payload.getExpiration(), payload.getId(), payload.getIssuedAt()));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean isMatchToken = new AntPathMatcher().match("/token", request.getRequestURI());
        boolean isMatchUserInfo = new AntPathMatcher().match("/userInfo", request.getRequestURI());
        return !((isMatchToken||isMatchUserInfo));
    }

}
