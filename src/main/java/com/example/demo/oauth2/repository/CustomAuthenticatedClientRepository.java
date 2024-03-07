package com.example.demo.oauth2.repository;

import com.example.demo.domain.OAuth2UserInfo;
import com.example.demo.domain.User;
import com.example.demo.domain.UserAuthorities;
import com.example.demo.dto.converter.NaverUserConverter;
import com.example.demo.dto.converter.OAuth2AuthorizedClientConverterFactory;
import com.example.demo.dto.en.ROLE;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import com.example.demo.repository.data.OAuth2UserInfoRepository;
import com.example.demo.repository.data.UserAuthoritiesRepository;
import com.example.demo.repository.data.UserRepository;
import com.example.demo.dto.*;
import com.example.demo.util.Check;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CustomAuthenticatedClientRepository implements OAuth2AuthorizedClientRepository {
    private final OAuth2UserInfoRepository oAuth2UserInfoRepository;
    private final UserRepository userRepository;
    private final UserAuthoritiesRepository userAuthoritiesRepository;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthenticatedPrincipal responsePrincipal = (OAuth2AuthenticatedPrincipal)principal.getPrincipal();
        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        OAuth2AuthorizedClientUserInfoDto oAuth2AuthorizedClientUserInfo = new OAuth2AuthorizedClientUserInfoDto(
                registrationId,
                responsePrincipal.getAttributes()
        );

        saveOAuth2Response(registrationId, oAuth2AuthorizedClientUserInfo);
    }

    public void saveOAuth2Response(String registrationId, OAuth2AuthorizedClientUserInfoDto oAuth2AuthorizedClientUserInfo) {
        boolean isSuccess = false;
        switch (registrationId){
            case "naver"->{
                NaverOAuth2Dto naverUser = OAuth2AuthorizedClientConverterFactory.getConverter(NaverOAuth2Dto.class).convert(oAuth2AuthorizedClientUserInfo);
                if (naverUser!=null){
                    isSuccessNaverOAuth2Dto(naverUser, registrationId);
                    isSuccess = true;
                }
                else isFailedNaverOAuth2Dto(registrationId);
            }
            case "kakao"->{
                log.info("{}", oAuth2AuthorizedClientUserInfo.getAttributes());
                log.info("사업자 등록해야 사용자 정보가 보입니다. ㅠ");
            }
        }
        if (!isSuccess)throw new CustomException(StatusCode.Internal_Server_Error);
    }

    private void isFailedNaverOAuth2Dto(String registrationId) {
        throw new CustomException(StatusCode.AUTHENTICATION_FAILED);
    }

    private void isSuccessNaverOAuth2Dto(NaverOAuth2Dto naverUser, String registrationId) {
        if (oAuth2UserInfoRepository.existsRegistrationIdAndId(registrationId, naverUser.getAttribute("id"))) return;
        User user = new NaverUserConverter().convert(naverUser);
        Check.notNull(user, StatusCode.UNSUPPORTED_OAUTH2_REQUIREMENT);
        User savedUser = userRepository.save(user);
        userAuthoritiesRepository.save(UserAuthorities.builder().user(savedUser).role(ROLE.ROLE_OAUTH2_USER).build());
        oAuth2UserInfoRepository.save(OAuth2UserInfo.builder().user(savedUser).registrationId(registrationId).oauth2Id(naverUser.getAttribute("id")).build());
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthenticatedPrincipal responsePrincipal = (OAuth2AuthenticatedPrincipal)principal.getPrincipal();
        OAuth2AuthorizedClientUserInfoDto oAuth2AuthorizedClientUserInfo = new OAuth2AuthorizedClientUserInfoDto(
                clientRegistrationId,
                responsePrincipal.getAttributes()
        );
        switch(clientRegistrationId){
            case "naver"->{
                NaverOAuth2Dto naverUser = OAuth2AuthorizedClientConverterFactory.getConverter(NaverOAuth2Dto.class).convert(oAuth2AuthorizedClientUserInfo);
                Check.notNull(naverUser.getAttribute("id"), StatusCode.UNSUPPORTED_OAUTH2_REQUIREMENT);
                userRepository.deleteByRegistrationIdAndOAuth2Id(clientRegistrationId, naverUser.getAttribute("id"));
            }
        }
    }
}
