package com.authorization.common.domain.authentication.controller;

import com.authorization.common.config.error.errorCode.MemberErrorCode;
import com.authorization.common.config.error.exception.AuthenticationFailedException;
import com.authorization.common.config.error.validator.MemberValidator;
import com.authorization.common.config.handler.UserServiceHandler;
import com.authorization.common.config.jwt.JwtTokenProvider;
import com.authorization.common.domain.authentication.model.request.AuthorizationRefreshRequest;
import com.authorization.common.domain.authentication.model.request.AuthorizationRequest;
import com.authorization.common.domain.authentication.model.response.AuthorizationResponse;
import com.authorization.common.domain.authentication.model.transfer.PrincipalDetails;
import com.authorization.common.domain.authentication.service.AuthenticationService;
import com.authorization.common.domain.oauth.model.enums.RedirectSocialAuthorizationCallbackType;
import com.authorization.common.domain.oauth.model.request.OAuthAuthorizationRequest;
import com.authorization.common.domain.oauth.model.response.OAuthAuthorizationResponse;
import com.authorization.common.domain.oauth.model.response.OAuthTokenResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeResponse;
import com.authorization.common.domain.oauth.model.transfer.ClientRegistration;
import com.authorization.common.domain.oauth.model.transfer.RedirectSocialAuthorizationPage;
import com.authorization.common.domain.oauth.repository.ClientRegistrationRepository;
import com.authorization.common.domain.oauth.repository.InMemoryOAuthAuthorizationRequestRepository;
import com.authorization.common.domain.oauth.service.OAuthService;
import com.authorization.common.domain.oauth.service.OAuthServiceFactory;
import com.authorization.domain.member.service.query.MemberQueryService;
import com.authorization.domain.memberSocial.enums.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final InMemoryOAuthAuthorizationRequestRepository inMemoryOAuthAuthorizationRequestRepository;
    private final MemberQueryService memberQueryService;
    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final MemberValidator memberValidator;
    private final UserServiceHandler userServiceHandler;

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponse> authenticateLogin(
            @RequestBody AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        memberValidator.authenticateLogin(authorizationRequest);

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authorizationRequest.getUsername(),
                            authorizationRequest.getPassword()
                    )
            );

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            String accessToken = authenticationService.generateAccessToken(principalDetails, request, response);
            String refreshToken = authenticationService.generateRefreshToken(principalDetails, request, response);

            AuthorizationResponse authorizationResponse = authenticationService.createAuthorizationResponse(accessToken, refreshToken, principalDetails);

            return ResponseEntity.ok().body(authorizationResponse);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException(MemberErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @PostMapping("/authorize/refresh")
    public ResponseEntity<AuthorizationResponse> authenticateRefresh(
            @RequestBody AuthorizationRefreshRequest authorizationRefreshRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String username = jwtTokenProvider.extractUsernameByRefreshToken(authorizationRefreshRequest.getRefreshToken());
        PrincipalDetails principalDetails = (PrincipalDetails) userServiceHandler.loadUserByUsername(username);

        //????????? ???????????????
        if (jwtTokenProvider.validateRefreshToken(authorizationRefreshRequest.getRefreshToken(), principalDetails.getUsername())) {

            String accessToken = authenticationService.generateAccessToken(principalDetails, request, response);
            String refreshToken = authenticationService.generateRefreshToken(principalDetails, request, response);

            AuthorizationResponse authorizationResponse = authenticationService.createAuthorizationResponse(accessToken, refreshToken, principalDetails);

            return ResponseEntity.ok().body(authorizationResponse);
        } else {
            throw new AuthenticationFailedException(MemberErrorCode.INVALID_TOKEN);
        }
    }

    /* ?????? ????????? ???????????? ???????????? (????????????) */
    @PostMapping("/logout")
    public ResponseEntity<?> authenticateLogout(HttpServletRequest request, HttpServletResponse response) {


        return ResponseEntity.ok("success");
    }

    /* ???????????? ?????? ????????? ????????? ?????? ??? ?????? ???????????? ????????? ???????????? ???????????? */
/*    @GetMapping("/oauth/authorize/{provider}")
    public void redirectSocialAuthorizationPage(
            @PathVariable Provider provider,
            @ModelAttribute RedirectSocialAuthorizationPageRequest redirectSocialAuthorizationPageRequest,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {

        memberValidator.redirectSocialAuthorizationPage(redirectSocialAuthorizationPageRequest);

        String state = generateState();

        // ???????????? ????????? ?????? ????????? ??????
        inMemoryOAuthAuthorizationRequestRepository.saveOAuthAuthorizationRequest(
                state,
                RedirectSocialAuthorizationPage.builder()
                        .referer(
                            request.getHeader("referer")
                        )
                        .redirectUri(redirectSocialAuthorizationPageRequest.getRedirectUri())
                        .redirectSocialAuthorizationCallbackType(redirectSocialAuthorizationPageRequest.getCallback())
                        .build()
        );

        ClientRegistration clientRegistration = clientRegistrationRepository.selectByRegistrationId(provider.getProvider());
        OAuthService oAuthService = OAuthServiceFactory.getOAuth2Service(provider, restTemplate);
        oAuthService.redirectAuthorize(clientRegistration, state, response);
    }*/

    /* ??? ?????? ?????????????????? ?????? ????????? ???????????? ???????????? */
    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<OAuthAuthorizationResponse> oAuthAuthenticationCallback(
            @PathVariable Provider provider,
            @ModelAttribute OAuthAuthorizationRequest oAuthAuthorizationRequest,
            HttpServletRequest request, HttpServletResponse response,
            @AuthenticationPrincipal PrincipalDetails loginUser
    ) throws Exception {

        //????????? ????????? ??? ???????????? request ????????? ????????????.
        RedirectSocialAuthorizationPage redirectSocialAuthorizationPage = inMemoryOAuthAuthorizationRequestRepository.
                deleteOAuthAuthorizationRequest(oAuthAuthorizationRequest.getState());

        //????????? ????????? ??????????????? ???????????? ??????????????? ????????? ??????????????? ??????
        if (oAuthAuthorizationRequest.getError() != null) {
            redirectWithErrorMessage(redirectSocialAuthorizationPage.getReferer(), oAuthAuthorizationRequest.getError(), response);
            return null;
        }

        //???????????? ????????? ?????? OAuth2 ??????????????? ????????? ????????????
        ClientRegistration clientRegistration = clientRegistrationRepository.selectByRegistrationId(provider.getProvider());
        OAuthService oAuthService = OAuthServiceFactory.getOAuth2Service(provider, restTemplate);

        //????????? ?????? ????????? ??????
        OAuthTokenResponse oAuthTokenResponse = oAuthService.getAccessToken(clientRegistration, oAuthAuthorizationRequest.getCode(), oAuthAuthorizationRequest.getState());
        OAuthMeResponse oAuthMeResponse = oAuthService.getMe(clientRegistration, oAuthTokenResponse.getAccessToken());

        String accessToken = null;

        //???????????? ?????? ?????? ??????
        if (RedirectSocialAuthorizationCallbackType.equalsLogin(redirectSocialAuthorizationPage.getRedirectSocialAuthorizationCallbackType())) {
            UserDetails userDetails = memberQueryService.loadUserByOAuth(provider, oAuthTokenResponse, oAuthMeResponse);
            accessToken = authenticationService.generateAccessToken(userDetails, request, response);
        }

        //?????? ????????? ?????? ?????? ??????
        else if (RedirectSocialAuthorizationCallbackType.equalsLink(redirectSocialAuthorizationPage.getRedirectSocialAuthorizationCallbackType())) {
            //????????? ????????? ?????????
            if (loginUser == null) {
                redirectWithErrorMessage(redirectSocialAuthorizationPage.getReferer(), "unauthorized", response);
                return null;
            }
            try {
                memberQueryService.linkOAuthAccount(loginUser.getUsername(), provider.getProvider(), oAuthTokenResponse, oAuthMeResponse);
            } catch (Exception e) {
                redirectWithErrorMessage(redirectSocialAuthorizationPage.getReferer(), e.getMessage(), response);
                return null;
            }
        }

        //?????? ??????
        response.sendRedirect(redirectSocialAuthorizationPage.getRedirectUri());

        OAuthAuthorizationResponse oAuthAuthorizationResponse = OAuthAuthorizationResponse.builder().accessToken(accessToken).build();
        return ResponseEntity.ok().body(oAuthAuthorizationResponse);
    }

    private void redirectWithErrorMessage(String uri, String message, HttpServletResponse response) throws IOException {

        String redirectUri = UriComponentsBuilder.fromUriString(uri)
                .replaceQueryParam("error", message).encode().build().toUriString();

        response.sendRedirect(redirectUri);
    }

    private String generateState() {

        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

}
