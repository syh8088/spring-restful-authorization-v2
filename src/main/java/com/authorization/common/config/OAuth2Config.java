package com.authorization.common.config;

import com.authorization.common.config.properties.OAuthClientProperties;
import com.authorization.common.domain.oauth.model.transfer.ClientRegistration;
import com.authorization.common.domain.oauth.repository.ClientRegistrationRepository;
import com.authorization.domain.memberSocial.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final OAuthClientProperties oAuthClientProperties;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        List<ClientRegistration> registrations = oAuthClientProperties.getRegistration()
                .keySet().stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new ClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {

        Provider provider = Provider.getByProvider(client);

        return ClientRegistration.builder()
                .registrationId(provider)
                .clientId(oAuthClientProperties.getRegistration().get(client).getClientId())
                .clientSecret(oAuthClientProperties.getRegistration().get(client).getClientSecret())
                .authorizationGrantType(oAuthClientProperties.getRegistration().get(client).getAuthorizationGrantType())
                .redirectUri(oAuthClientProperties.getRegistration().get(client).getRedirectUri())
                .scopes(oAuthClientProperties.getRegistration().get(client).getScope())
                .authorizationUri(oAuthClientProperties.getProvider().get(client).getAuthorizationUri())
                .tokenUri(oAuthClientProperties.getProvider().get(client).getTokenUri())
                .userInfoUri(oAuthClientProperties.getProvider().get(client).getUserInfoUri())
                .unlinkUri(oAuthClientProperties.getProvider().get(client).getUnlinkUri())
                .build();
    }
}
