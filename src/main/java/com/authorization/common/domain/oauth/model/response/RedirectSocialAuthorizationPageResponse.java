package com.authorization.common.domain.oauth.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedirectSocialAuthorizationPageResponse {

    private String socialLoginPage;

    @Builder
    public RedirectSocialAuthorizationPageResponse(String socialLoginPage) {
        this.socialLoginPage = socialLoginPage;
    }
}
