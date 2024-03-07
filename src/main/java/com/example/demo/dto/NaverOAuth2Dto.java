package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class NaverOAuth2Dto implements OAuth2UserInfoDto{

    private String resultCode;

    private String message;

    private Map<String,Object> response;

    @Override
    public Map<String, Object> getAttributes() {
        return response;
    }

    @Override
    public String getAttribute(String key) {
        return (String) getAttributes().get(key);
    }
}
