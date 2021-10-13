package com.authorization.common.domain.oauth.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RedirectSocialAuthorizationCallbackType {

    LOGIN("login"),
    LINK("link"),
    NONE("none");

    private final String redirectSocialAuthorizationCallbackType;

    RedirectSocialAuthorizationCallbackType(
            String redirectSocialAuthorizationCallbackType
    ) {

        this.redirectSocialAuthorizationCallbackType = redirectSocialAuthorizationCallbackType;
    }

    public String getRedirectSocialAuthorizationCallbackType() {
        return this.redirectSocialAuthorizationCallbackType;
    }

    public static RedirectSocialAuthorizationCallbackType getByRedirectSocialAuthorizationCallback(String redirectSocialAuthorizationCallbackType) {
        return Arrays.stream(RedirectSocialAuthorizationCallbackType.values())
                .filter(data -> data.getRedirectSocialAuthorizationCallbackType().equals(redirectSocialAuthorizationCallbackType))
                .findFirst()
                .orElse(RedirectSocialAuthorizationCallbackType.NONE);
    }

    public static boolean equalsLogin(RedirectSocialAuthorizationCallbackType redirectSocialAuthorizationCallbackType) {
        return (RedirectSocialAuthorizationCallbackType.LOGIN.equals(redirectSocialAuthorizationCallbackType));
    }

    public static boolean equalsLink(RedirectSocialAuthorizationCallbackType redirectSocialAuthorizationCallbackType) {
        return (RedirectSocialAuthorizationCallbackType.LINK.equals(redirectSocialAuthorizationCallbackType));
    }

    public static boolean equalsNone(RedirectSocialAuthorizationCallbackType redirectSocialAuthorizationCallbackType) {
        return (RedirectSocialAuthorizationCallbackType.NONE.equals(redirectSocialAuthorizationCallbackType));
    }
}
