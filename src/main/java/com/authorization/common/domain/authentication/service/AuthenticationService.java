package com.authorization.common.domain.authentication.service;

import com.authorization.common.config.jwt.JwtTokenProvider;
import com.authorization.common.config.properties.JwtProperties;
import com.authorization.common.domain.authentication.model.response.AuthorizationResponse;
import com.authorization.common.domain.authentication.model.transfer.PrincipalDetails;
import com.authorization.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthorizationResponse createAuthorizationResponse(
            String accessToken, String refreshToken, PrincipalDetails principalDetails
    ) {

        return AuthorizationResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(jwtProperties.getAccessTokenExpired())
                .member_seq(principalDetails.getId())
                .member_id(principalDetails.getUsername())
                .authorities(principalDetails.getAuthorities())
                .build();
    }

    public String generateAccessToken(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {

        boolean secure = request.isSecure();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails.getUsername());

        final int cookieMaxAge = jwtTokenProvider.getAccessTokenExpirationDate().intValue();
        CookieUtils.addCookie(response, "access_token", jwtTokenProvider.generateAccessToken(userDetails.getUsername()), true, secure, cookieMaxAge);
        return accessToken;
    }

    public String generateRefreshToken(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {

        boolean secure = request.isSecure();
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

        return refreshToken;
    }
}
