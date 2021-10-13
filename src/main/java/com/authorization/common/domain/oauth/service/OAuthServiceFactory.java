package com.authorization.common.domain.oauth.service;

import com.authorization.common.config.error.errorCode.MemberErrorCode;
import com.authorization.common.config.error.exception.CommonException;
import com.authorization.domain.memberSocial.enums.Provider;
import org.springframework.web.client.RestTemplate;

public class OAuthServiceFactory {

    public static OAuthService getOAuth2Service(Provider provider, RestTemplate restTemplate) {

        OAuthService oAuthService = provider.oAuthServiceCalculate(restTemplate);
        if (oAuthService == null) {
            throw new CommonException(MemberErrorCode.NOT_FOUND_PROVIDER, new Object[] { provider.getProvider().toUpperCase() });
        }

        return oAuthService;
    }
}
