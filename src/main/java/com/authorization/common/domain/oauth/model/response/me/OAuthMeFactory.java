package com.authorization.common.domain.oauth.model.response.me;

import com.authorization.common.config.error.errorCode.MemberErrorCode;
import com.authorization.common.config.error.exception.CommonException;
import com.authorization.domain.memberSocial.enums.Provider;

import java.util.Map;

public class OAuthMeFactory {

    public static OAuthMeResponse getOAuthMe(Provider provider, Map<String, Object> attributes) {

        OAuthMeResponse oAuthMeResponse = provider.oAuthMeCalculate(attributes);
        if (oAuthMeResponse == null) {
            throw new CommonException(MemberErrorCode.NOT_FOUND_PROVIDER, new Object[] { provider.getProvider().toUpperCase() });
        }

        return oAuthMeResponse;
    }
}