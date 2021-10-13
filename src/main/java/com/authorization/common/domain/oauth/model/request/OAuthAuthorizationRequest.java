package com.authorization.common.domain.oauth.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class OAuthAuthorizationRequest {
    private String state;
    private String code;
    private String error;

    public OAuthAuthorizationRequest(String state, String code, String error) {
        this.state = state;
        this.code = code;
        this.error = error;
    }
}
