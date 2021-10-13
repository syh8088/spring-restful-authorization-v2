package com.authorization.common.domain.oauth.model.response.me;

import java.util.Map;

public class CommonOAuthMeResponse extends OAuthMeResponse {

    public CommonOAuthMeResponse(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}

