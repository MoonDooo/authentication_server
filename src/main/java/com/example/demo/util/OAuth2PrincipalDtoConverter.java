package com.example.demo.util;

import com.example.demo.dto.NaverOAuth2Dto;
import com.example.demo.dto.OAuth2AuthorizedClientUserInfoDto;
import com.example.demo.dto.OAuth2UserInfoDto;
import com.example.demo.exception.StatusCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.Map;

public class OAuth2PrincipalDtoConverter<T extends OAuth2UserInfoDto> implements Converter<OAuth2AuthorizedClientUserInfoDto, T> {
    private Class<T> type;

    public OAuth2PrincipalDtoConverter(Class<T> type) {
        this.type = type;
    }


    @Override
    public T convert(OAuth2AuthorizedClientUserInfoDto source) {
        Check.notNull(type, StatusCode.UNSUPPORTED_OAUTH2_REQUIREMENT);
        Check.notNull(source, StatusCode.Internal_Server_Error);
        Map<String, Object> attributes = source.getAttributes();
        switch (source.getRegistrationId()){
            case "naver"->{
                if (attributes.get("response")==null||attributes.get("message")==null||attributes.get("resultcode")==null) return null;
                return type.cast(NaverOAuth2Dto.builder()
                        .response((Map<String, Object>) attributes.get("response"))
                        .message((String) attributes.get("message"))
                        .resultCode((String) attributes.get("resultcode"))
                        .build());
            }
        }

        return null;
    }

}
