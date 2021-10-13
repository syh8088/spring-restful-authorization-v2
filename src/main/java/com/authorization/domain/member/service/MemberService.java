package com.authorization.domain.member.service;

import com.authorization.domain.member.model.entity.Member;
import com.authorization.domain.member.repository.MemberRepository;
import com.authorization.domain.memberSocial.model.entity.MemberSocial;
import com.authorization.domain.memberSocial.repository.MemberSocialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMember(Member member) {
        System.out.println("LogicService --> currentTransactionName : {}  = " +  TransactionSynchronizationManager.getCurrentTransactionName());
        memberRepository.save(member);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberSocial(MemberSocial memberSocial) {

        memberSocialRepository.save(memberSocial);
    }
}
