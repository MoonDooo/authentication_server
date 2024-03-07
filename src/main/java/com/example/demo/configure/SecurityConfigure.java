package com.example.demo.configure;

import com.example.demo.filter.CustomExceptionHandlerFilter;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.filter.JwtBlackListFilter;
import com.example.demo.filter.JwtRefreshFilter;
import com.example.demo.oauth2.custom.CustomAuthenticationFailureHandler;
import com.example.demo.oauth2.repository.CustomAuthenticatedClientRepository;
import com.example.demo.oauth2.custom.CustomAuthenticationEntryPoint;
import com.example.demo.oauth2.custom.CustomAuthenticationSuccessHandler;
import com.example.demo.oauth2.custom.CustomOAuth2RegisterFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.AntPathMatcher;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfigure {
    @Autowired private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired private CustomAuthenticatedClientRepository customAuthenticatedClientRepository;
    @Autowired private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired private CustomExceptionHandlerFilter customExceptionHandlerFilter;
    @Autowired private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired private JwtBlackListFilter jwtBlackListFilter;
    @Autowired private JwtRefreshFilter jwtRefreshFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(auth -> auth
                        .authorizationEndpoint(end->end
                                .authorizationRequestRepository(authorizationRequestRepository)
                                .authorizationRedirectStrategy(redirectStrategy)
                        )
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .authorizedClientRepository(customAuthenticatedClientRepository)
                )
                .addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(jwtRefreshFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(customExceptionHandlerFilter, JwtRefreshFilter.class)
                .addFilterAfter(jwtBlackListFilter, JwtAuthenticationFilter.class)
                .rememberMe(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex->ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/oauth2/authorization/**").authenticated()
                        .requestMatchers("/login/oauth2/code/**").authenticated()
                        .requestMatchers("/token").authenticated()
                        .requestMatchers("/userInfo").authenticated()
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    @Autowired private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired private RedirectStrategy redirectStrategy;
    @Autowired private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;


    @Bean
    public CustomOAuth2RegisterFilter customOAuth2RegisterFilter() {
        String authorizationRequestBaseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
        OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        return new CustomOAuth2RegisterFilter(redirectStrategy, defaultAuthorizationRequestResolver, authorizationRequestRepository);
    }


}
