package com.example.demo.dto.converter;

import com.example.demo.dto.OAuth2AuthorizedClientUserInfoDto;
import com.example.demo.dto.OAuth2UserInfoDto;
import com.example.demo.util.OAuth2PrincipalDtoConverter;
import org.springframework.core.convert.converter.Converter;

public class OAuth2AuthorizedClientConverterFactory {
    public static <T extends OAuth2UserInfoDto> Converter<OAuth2AuthorizedClientUserInfoDto, T> getConverter(Class<T> type) {
        return new OAuth2PrincipalDtoConverter<>(type);
    }
}
