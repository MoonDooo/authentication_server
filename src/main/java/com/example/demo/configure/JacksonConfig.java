package com.example.demo.configure;

import com.example.demo.repository.redis.OAuth2AuthorizationRequestDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomDeserialization() {
        return jacksonObjectMapperBuilder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(OAuth2AuthorizationRequest.class, new OAuth2AuthorizationRequestDeserializer());
            jacksonObjectMapperBuilder.modulesToInstall(module);
        };
    }
}
