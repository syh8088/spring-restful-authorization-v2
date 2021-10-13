package com.authorization.common.domain.oauth.repository;

import com.authorization.common.domain.oauth.model.transfer.RedirectSocialAuthorizationPage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryOAuthAuthorizationRequestRepository {

    private Map<String, RedirectSocialAuthorizationPage> oAuthAuthorizationRequestMap = new HashMap<>();

    public void saveOAuthAuthorizationRequest(String state, RedirectSocialAuthorizationPage redirectSocialAuthorizationPage){
        oAuthAuthorizationRequestMap.put(state, redirectSocialAuthorizationPage);
    }

    public RedirectSocialAuthorizationPage getOAuthAuthorizationRequest(String state){
        return oAuthAuthorizationRequestMap.get(state);
    }

    public RedirectSocialAuthorizationPage deleteOAuthAuthorizationRequest(String state){
        return oAuthAuthorizationRequestMap.remove(state);
    }
}
