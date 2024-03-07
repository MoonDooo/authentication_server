package com.example.demo.repository.redis;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class OAuth2AuthorizationRequestDeserializer extends JsonDeserializer<OAuth2AuthorizationRequest> {
    @Override
    public OAuth2AuthorizationRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode();

        ObjectMapper objectMapper = new ObjectMapper();
        return builder
                .authorizationUri(jsonNode.get("authorizationUri").asText())
                .clientId(jsonNode.get("clientId").asText())
                .redirectUri(jsonNode.get("redirectUri").asText())
                .scopes(objectMapper.convertValue(jsonNode.get("scopes"), Set.class))
                .state(jsonNode.get("state").asText())
                .additionalParameters(objectMapper.convertValue(jsonNode.get("additionalParameters"), Map.class))
                .authorizationRequestUri(jsonNode.get("authorizationRequestUri").asText())
                .attributes(objectMapper.convertValue(jsonNode.get("attributes"), Map.class))
                .build();

    }
}
