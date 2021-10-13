package com.authorization.common.domain.oauth.service;

import com.authorization.common.config.error.errorCode.MemberErrorCode;
import com.authorization.common.config.error.exception.OAuthFailedException;
import com.authorization.common.domain.oauth.model.transfer.ClientRegistration;
import com.authorization.common.domain.oauth.model.response.OAuthTokenResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeFactory;
import com.authorization.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public abstract class OAuthService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final RestTemplate restTemplate;

    protected OAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void redirectAuthorize(
            ClientRegistration clientRegistration, String state,
            HttpServletResponse response
    ) throws IOException {

        String authorizationUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("response_type", "code")
                .queryParam("access_type", "offline") // refresh accessToken 을 받기 위한 옵션 (구글 전용)
                .queryParam("include_granted_scopes", true) // google 전용
                .queryParam("scope", String.join("+", clientRegistration.getScopes()))
                .queryParam("state", state)
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .build().encode(StandardCharsets.UTF_8).toUriString();

        response.sendRedirect(authorizationUri);
    }

    public String redirectAuthorize2(
            ClientRegistration clientRegistration, String state,
            HttpServletResponse response
    ) throws IOException {

        String authorizationUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("response_type", "code")
                .queryParam("access_type", "offline") // refresh accessToken 을 받기 위한 옵션 (구글 전용)
                .queryParam("include_granted_scopes", true) // google 전용
                .queryParam("scope", String.join("+", clientRegistration.getScopes()))
                .queryParam("state", state)
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .build().encode(StandardCharsets.UTF_8).toUriString();

        return authorizationUri;
    }

    public OAuthTokenResponse getAccessToken(ClientRegistration clientRegistration, String code, String state) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientRegistration.getClientId());
        params.add("client_secret", clientRegistration.getClientSecret());
        params.add("grant_type", clientRegistration.getAuthorizationGrantType());
        params.add("code", code);
        params.add("state", state);
        params.add("redirect_uri", clientRegistration.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> entity = null;
        try {
            entity = restTemplate.exchange(clientRegistration.getProviderDetails().getTokenUri(), HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException exception) {
            int statusCode = exception.getStatusCode().value();
          //  throw new OAuth2RequestFailedException(String.format("%s 토큰 요청 실패 [응답코드 : %d].", clientRegistration.getRegistrationId().toUpperCase(), statusCode), exception);
        }

        log.debug(entity.getBody());
        JsonObject jsonObj = JsonUtils.parse(entity.getBody()).getAsJsonObject();
        String accessToken = jsonObj.get("access_token").getAsString();
        String refreshToken = jsonObj.get("refresh_token").getAsString();
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(jsonObj.get("expires_in").getAsLong());

        return new OAuthTokenResponse(accessToken, refreshToken, expiredAt);
    }

    protected OAuthTokenResponse refreshOAuth2Token(ClientRegistration clientRegistration, OAuthTokenResponse token) {

        //토큰이 만료되지 않았다면 원래 토큰을 리턴
        if (LocalDateTime.now().isBefore(token.getExpiredAt())) return token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientRegistration.getClientId());
        params.add("client_secret", clientRegistration.getClientSecret());
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", token.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> entity = null;
        try {
            entity = restTemplate.exchange(clientRegistration.getProviderDetails().getTokenUri(), HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException exception) {
            int statusCode = exception.getStatusCode().value();
          //  throw new OAuth2RequestFailedException(String.format("%s 토큰 갱신 실패 [응답코드 : %d].", clientRegistration.getRegistrationId().toUpperCase(), statusCode), exception);
        }

        JsonObject jsonObj = JsonUtils.parse(entity.getBody()).getAsJsonObject();
        String accessToken = jsonObj.get("access_token").getAsString();
        //naver의 경우는 null
        Optional<JsonElement> optionalNewRefreshToken = Optional.ofNullable(jsonObj.get("refresh_token"));
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(jsonObj.get("expires_in").getAsLong());

        return new OAuthTokenResponse(accessToken, optionalNewRefreshToken.isPresent() ? optionalNewRefreshToken.get().getAsString() : token.getRefreshToken(), expiredAt);
    }

    public OAuthMeResponse getMe(ClientRegistration clientRegistration, String accessToken) {

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> entity = null;
        try {
            entity = restTemplate.exchange(clientRegistration.getProviderDetails().getUserInfoUri(), HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException exception) {

            int statusCode = exception.getStatusCode().value();
            throw new OAuthFailedException(MemberErrorCode.SOCIAL_GET_ME_ERROR, new Object[] { clientRegistration.getRegistrationId().getProvider().toUpperCase(), statusCode });
        }

        log.debug(entity.getBody());
        Map<String, Object> memberAttributes = JsonUtils.fromJson(entity.getBody(), Map.class);

        OAuthMeResponse oAuthMeResponse = OAuthMeFactory.getOAuthMe(clientRegistration.getRegistrationId(), memberAttributes);

        return oAuthMeResponse;
    }

    public abstract void unlink(ClientRegistration clientRegistration, OAuthTokenResponse token);
}
