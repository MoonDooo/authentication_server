package com.example.demo.oauth2.custom;

import com.example.demo.domain.User;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.oauth2.OAuth2LoginRegistrationIdResolver;
import com.example.demo.repository.data.UserRepository;
import com.example.demo.service.UserJwtTokenService;
import com.example.demo.dto.NaverOAuth2Dto;
import com.example.demo.dto.converter.OAuth2AuthorizedClientConverterFactory;
import com.example.demo.dto.OAuth2AuthorizedClientUserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2LoginRegistrationIdResolver oAuth2LoginRegistrationIdResolver;
    private final UserRepository userRepository;
    private final UserJwtTokenService userJwtTokenService;
    @Override
    @SneakyThrows
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String registrationId = oAuth2LoginRegistrationIdResolver.getRegistrationId(request);
        OAuth2AuthenticatedPrincipal responsePrincipal = (OAuth2AuthenticatedPrincipal)authentication.getPrincipal();
        OAuth2AuthorizedClientUserInfoDto oAuth2AuthorizedClientUserInfo = new OAuth2AuthorizedClientUserInfoDto(
                registrationId,
                responsePrincipal.getAttributes()
        );

        switch (registrationId){
            case "naver"->{
                NaverOAuth2Dto naverUser = OAuth2AuthorizedClientConverterFactory.getConverter(NaverOAuth2Dto.class).convert(oAuth2AuthorizedClientUserInfo);
                String id = Objects.requireNonNull(naverUser).getAttribute("id");
                Optional<User> findUser = userRepository.findByRegistrationIdAndOauth2IdWithOAuth2UserAndAuthorities(registrationId, id);
                if (findUser.isPresent()){
                    response.setContentType(ContentType.APPLICATION_JSON.getType());
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(userJwtTokenService.createJWT(findUser.get())));
                }else{
                    log.debug("유저 정보를 찾을 수 없습니다 > CustomAuthenticationSuccessHandler.onAuthenticationSuccess");
                    throw new CustomException(StatusCode.Internal_Server_Error);
                }
            }
            case "kakao"->{
                log.info("추후 개발 > 사업자 등록 전 사용자 정보 가져올 수 없음");
            }
        }

    }
}
