package com.authorization.common.domain.oauth.model.transfer;

import com.authorization.common.domain.oauth.model.enums.RedirectSocialAuthorizationCallbackType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedirectSocialAuthorizationPage {

    private String referer;
    private String redirectUri;
    private RedirectSocialAuthorizationCallbackType redirectSocialAuthorizationCallbackType;

    @Builder
    public RedirectSocialAuthorizationPage(
            String referer, String redirectUri,
            RedirectSocialAuthorizationCallbackType redirectSocialAuthorizationCallbackType
    ) {

        this.referer = referer;
        this.redirectUri = redirectUri;
        this.redirectSocialAuthorizationCallbackType = redirectSocialAuthorizationCallbackType;
    }
}
