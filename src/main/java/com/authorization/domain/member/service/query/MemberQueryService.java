package com.authorization.domain.member.service.query;

import com.authorization.common.domain.authentication.model.transfer.PrincipalDetails;
import com.authorization.common.domain.oauth.model.response.OAuthTokenResponse;
import com.authorization.common.domain.oauth.model.response.me.OAuthMeResponse;
import com.authorization.domain.config.model.entity.Config;
import com.authorization.domain.config.service.ConfigService;
import com.authorization.domain.member.enums.MemberType;
import com.authorization.domain.member.model.entity.Member;
import com.authorization.domain.member.repository.MemberRepository;
import com.authorization.domain.member.service.MemberService;
import com.authorization.domain.memberSocial.enums.Provider;
import com.authorization.domain.memberSocial.model.entity.MemberSocial;
import com.authorization.domain.memberSocial.repository.MemberSocialRepository;
import com.authorization.domain.role.model.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberService memberService;
    private final ConfigService configService;
    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;

    public Member selectMemberById(String username) {
        return memberRepository.findByIdAndUseYn(username, true);
    }

    public UserDetails loadUserByOAuth(Provider provider, OAuthTokenResponse oAuthTokenResponse, OAuthMeResponse oAuthMeResponse) {

        MemberSocial memberSocial = memberSocialRepository.selectMemberSocialByProviderAndProviderId(provider, oAuthMeResponse.getId());
        Member member = null;
        System.out.println("LogicService --> currentTransactionName : {}  = " +  TransactionSynchronizationManager.getCurrentTransactionName());

        if (memberSocial != null) {

            member = memberSocial.getMember();
            memberSocial.updateToken(oAuthTokenResponse.getAccessToken(), oAuthTokenResponse.getRefreshToken(), oAuthTokenResponse.getExpiredAt());

            memberService.saveMemberSocial(memberSocial);
        } else {

            MemberSocial newMemberSocial = MemberSocial.builder()
                    .provider(provider)
                    .providerId(oAuthMeResponse.getId())
                    .accessToken(oAuthTokenResponse.getAccessToken())
                    .refreshToken(oAuthTokenResponse.getRefreshToken())
                    .expiredAt(oAuthTokenResponse.getExpiredAt()).build();

            String id = provider.getProvider() + "_" + oAuthMeResponse.getId();

            Config config = configService.selectConfig();
            Role clientRole = config.getClientRole();

            if (StringUtils.isNotBlank(oAuthMeResponse.getEmail())) {

                member = memberRepository.findByEmail(oAuthMeResponse.getEmail())
                        .orElse(Member.builder()
                                .email(oAuthMeResponse.getEmail())
                                .name(oAuthMeResponse.getName())
                                .id(id)
                                .todayLogin(LocalDateTime.now())
                                .role(clientRole)
                                .build());
            } else {
                member = Member.builder()
                        .email(oAuthMeResponse.getEmail())
                        .name(oAuthMeResponse.getName())
                        .id(id)
                        .todayLogin(LocalDateTime.now())
                        .role(clientRole)
                        .build();
            }

            member.setMemberType(MemberType.OAUTH);
            member.setMemberSocial(newMemberSocial);
            memberService.saveMember(member);
        }

        return PrincipalDetails.builder()
                .id(member.getMemberNo())
                .username(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberType(member.getMemberType())
                .authorities(member.getAuthorities()).build();
    }

    public void linkOAuthAccount(String username, String provider, OAuthTokenResponse oAuthTokenResponse, OAuthMeResponse oAuthMeResponse) {

    }
}
