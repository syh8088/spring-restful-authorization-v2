package com.authorization.domain.memberSocial.enums;

import com.authorization.common.domain.oauth.model.response.me.GoogleOAuthMeResponse;
import com.authorization.common.domain.oauth.model.response.me.KakaoOAuthMeResponse;
import com.authorization.common.domain.oauth.model.response.me.NaverOAuthMeResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeResponse;
import com.authorization.common.domain.oauth.service.GoogleOAuthService;
import com.authorization.common.domain.oauth.service.KakaoOAuthService;
import com.authorization.common.domain.oauth.service.NaverOAuthService;
import com.authorization.common.domain.oauth.service.OAuthService;
import lombok.Getter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
public enum Provider {

    GOOGLE("google", GoogleOAuthMeResponse::new, GoogleOAuthService::new),
    KAKAO("kakao", KakaoOAuthMeResponse::new, KakaoOAuthService::new),
    NAVER("naver", NaverOAuthMeResponse::new, NaverOAuthService::new),
    NONE("none", attributes -> null, restTemplate -> null);

    private final String provider;
    private final Function<Map<String, Object>, OAuthMeResponse> expression;
    private final Function<RestTemplate, OAuthService> expression2;

    Provider(
            String provider,
            Function<Map<String, Object>, OAuthMeResponse> expression,
            Function<RestTemplate, OAuthService> expression2
    ) {

        this.provider = provider;
        this.expression = expression;
        this.expression2 = expression2;
    }

    public String getProvider() {
        return this.provider;
    }

    public static Provider getByProvider(String provider) {
        return Arrays.stream(Provider.values())
                .filter(data -> data.getProvider().equals(provider))
                .findFirst()
                .orElse(Provider.NONE);
    }

    public OAuthMeResponse oAuthMeCalculate(Map<String, Object> attributes) {
        return expression.apply(attributes);
    }

    public OAuthService oAuthServiceCalculate(RestTemplate restTemplate) {
        return expression2.apply(restTemplate);
    }
}
