package com.authorization.common.config.error.validator;

import com.authorization.common.domain.authentication.model.request.AuthorizationRequest;
import com.authorization.common.config.error.errorCode.MemberErrorCode;
import com.authorization.common.config.error.exception.CommonException;
import com.authorization.common.domain.oauth.model.enums.RedirectSocialAuthorizationCallbackType;
import com.authorization.common.domain.oauth.model.request.OAuthAuthorizationLoginRequest;
import com.authorization.common.domain.oauth.model.request.RedirectSocialAuthorizationPageRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {

    public void authenticateLogin(AuthorizationRequest authorizationRequest) {

        if (StringUtils.isBlank(authorizationRequest.getUsername()) || StringUtils.isBlank(authorizationRequest.getPassword())) {
            throw new CommonException(MemberErrorCode.NOT_EXIST_USERNAME_OR_PASSWORD);
        }
    }

    public void redirectSocialAuthorizationPage(RedirectSocialAuthorizationPageRequest redirectSocialAuthorizationPageRequest) {

        if (
                StringUtils.isBlank(redirectSocialAuthorizationPageRequest.getRedirectUri()) ||
                RedirectSocialAuthorizationCallbackType.equalsNone(redirectSocialAuthorizationPageRequest.getCallback())
        ) {
            throw new CommonException(MemberErrorCode.NOT_EXIST_REDIRECT_SOCIAL_AUTHORIZATION_PAGE_PARAMETER);
        }
    }

    public void authenticateSocialLogin(OAuthAuthorizationLoginRequest oAuthAuthorizationLoginRequest) {

        if (
                StringUtils.isBlank(oAuthAuthorizationLoginRequest.getAccessToken()) ||
                StringUtils.isBlank(oAuthAuthorizationLoginRequest.getRefreshToken()) ||
                oAuthAuthorizationLoginRequest.getRefreshToken() == null
        ) {
            throw new CommonException(MemberErrorCode.NOT_EXIST_AUTHENTICATE_SOCIAL_LOGIN_PARAMETER);
        }
    }
}
