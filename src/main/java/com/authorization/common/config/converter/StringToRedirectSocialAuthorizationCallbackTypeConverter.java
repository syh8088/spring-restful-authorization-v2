package com.authorization.common.config.converter;

import com.authorization.common.domain.oauth.model.enums.RedirectSocialAuthorizationCallbackType;
import org.springframework.core.convert.converter.Converter;

public class StringToRedirectSocialAuthorizationCallbackTypeConverter implements Converter<String, RedirectSocialAuthorizationCallbackType> {

    @Override
    public RedirectSocialAuthorizationCallbackType convert(String source) {

        return RedirectSocialAuthorizationCallbackType.getByRedirectSocialAuthorizationCallback(source);
    }
}
