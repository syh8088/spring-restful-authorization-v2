package com.authorization.common.domain.oauth.model.response.me;

import java.util.Map;

public abstract class OAuthMeResponse {

    public Map<String, Object> attributes;

    public OAuthMeResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();
}
