package com.authorization.common.domain.oauth.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class OAuthAuthorizationLoginRequest {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiredAt;

    public OAuthAuthorizationLoginRequest(String accessToken, String refreshToken, LocalDateTime expiredAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }
}
