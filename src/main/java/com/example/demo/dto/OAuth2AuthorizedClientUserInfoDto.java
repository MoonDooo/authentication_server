package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class OAuth2AuthorizedClientUserInfoDto {
    private String registrationId;
    private Map<String, Object> attributes;
}
