package com.authorization.common.domain.oauth.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class OAuthAuthorizationResponse {
    private String accessToken;

    @Builder
    public OAuthAuthorizationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
