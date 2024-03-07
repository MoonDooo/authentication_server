package com.example.demo.dto;

import java.util.Map;

public interface OAuth2UserInfoDto {
    Map<String, Object> getAttributes();

    String getAttribute(String key);
}
