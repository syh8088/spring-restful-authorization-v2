package com.authorization.common.domain.oauth.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthTokenResponse {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiredAt;

    @Builder
    public OAuthTokenResponse(String accessToken, String refreshToken, LocalDateTime expiredAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }
}
