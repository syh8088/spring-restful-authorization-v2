package com.authorization.common.domain.oauth.model.response.me;

import java.util.Map;

public class KakaoOAuthMeResponse extends OAuthMeResponse {

    public KakaoOAuthMeResponse(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        return (String) parsingProperties("properties").get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) parsingProperties("kakao_account").get("email");
    }

    private Map<String, Object> parsingProperties(String key) {
        return (Map<String, Object>) attributes.get(key);
    }

    private Map<String, Object> parsingProfile(String key) {
        return (Map<String, Object>) parsingProperties(key).get("profile");
    }
}
