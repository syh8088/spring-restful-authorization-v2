package com.authorization.common.domain.authentication.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationRefreshRequest {

    private String refreshToken;

    @Builder
    public AuthorizationRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
