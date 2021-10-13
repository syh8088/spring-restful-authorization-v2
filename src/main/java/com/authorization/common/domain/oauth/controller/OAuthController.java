package com.authorization.common.domain.oauth.controller;

import com.authorization.common.config.error.validator.MemberValidator;
import com.authorization.common.domain.authentication.model.response.AuthorizationResponse;
import com.authorization.common.domain.authentication.model.transfer.PrincipalDetails;
import com.authorization.common.domain.authentication.service.AuthenticationService;
import com.authorization.common.domain.oauth.model.request.OAuthAuthorizationLoginRequest;
import com.authorization.common.domain.oauth.model.response.OAuthTokenResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeResponse;
import com.authorization.common.domain.oauth.model.transfer.ClientRegistration;
import com.authorization.common.domain.oauth.repository.ClientRegistrationRepository;
import com.authorization.common.domain.oauth.service.OAuthService;
import com.authorization.common.domain.oauth.service.OAuthServiceFactory;
import com.authorization.domain.member.service.query.MemberQueryService;
import com.authorization.domain.memberSocial.enums.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final AuthenticationService authenticationService;
    private final MemberQueryService memberQueryService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final MemberValidator memberValidator;
    private final RestTemplate restTemplate;

    @PostMapping("/oauth/authorize/{provider}")
    public ResponseEntity<AuthorizationResponse> authenticateSocialLogin(
            @PathVariable Provider provider, @RequestBody OAuthAuthorizationLoginRequest oAuthAuthorizationLoginRequest,
            HttpServletRequest request, HttpServletResponse response
    ) {

        memberValidator.authenticateSocialLogin(oAuthAuthorizationLoginRequest);

        String accessToken = oAuthAuthorizationLoginRequest.getAccessToken();
        String refreshToken = oAuthAuthorizationLoginRequest.getRefreshToken();
        LocalDateTime expiredAt = oAuthAuthorizationLoginRequest.getExpiredAt();

        OAuthService oAuthService = OAuthServiceFactory.getOAuth2Service(provider, restTemplate);

        ClientRegistration clientRegistration = clientRegistrationRepository.selectByRegistrationId(provider.getProvider());
        OAuthMeResponse oAuthMeResponse = oAuthService.getMe(clientRegistration, accessToken);

        OAuthTokenResponse oAuthTokenResponse = OAuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .build();

        PrincipalDetails principalDetails = (PrincipalDetails) memberQueryService.loadUserByOAuth(provider, oAuthTokenResponse, oAuthMeResponse);
        String jwtAccessToken = authenticationService.generateAccessToken(principalDetails, request, response);
        String jwtRefreshToken = authenticationService.generateRefreshToken(principalDetails, request, response);

        AuthorizationResponse authorizationResponse = authenticationService.createAuthorizationResponse(jwtAccessToken, jwtRefreshToken, principalDetails);

        return ResponseEntity.ok().body(authorizationResponse);
    }

}
