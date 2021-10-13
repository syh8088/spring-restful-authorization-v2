package com.authorization.common.domain.oauth.model.request;

import com.authorization.common.domain.oauth.model.enums.RedirectSocialAuthorizationCallbackType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedirectSocialAuthorizationPageRequest {

    private String redirectUri;
    private RedirectSocialAuthorizationCallbackType callback;
}
