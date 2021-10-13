package com.authorization.common.domain.oauth.model.response.me;

import java.util.Map;

public class NaverOAuthMeResponse extends OAuthMeResponse {

    public NaverOAuthMeResponse(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) parsingProperties().get("id");
    }

    @Override
    public String getName() {
        return (String) parsingProperties().get("name");
    }

    @Override
    public String getEmail() {
        return (String) parsingProperties().get("email");
    }

    private Map<String, Object> parsingProperties() {
        return (Map<String, Object>) attributes.get("response");
    }
}
