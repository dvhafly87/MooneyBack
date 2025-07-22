package com.MooneyB.Challenge;

import com.MooneyB.Member.Member;
import com.MooneyB.Member.MemberRepo; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.MooneyB.common.exceptions.ChallengeNotFoundException;
import com.MooneyB.common.exceptions.MemberNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberRepo memberRepository; 

    // 생성자 주입
    public ChallengeService(ChallengeRepository challengeRepository, MemberRepo memberRepository) {
        this.challengeRepository = challengeRepository;
        this.memberRepository = memberRepository;
    }

    public Challenge createChallenge(Challenge challenge, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));
        challenge.setMember(member);

        // 챌린지 시작일이 종료일보다 늦지 않도록 유효성 검사 (DB DDL에 CHECK 제약조건이 있지만, 서비스 단에서 미리 검사)
        if (challenge.getMchlStartDate().isAfter(challenge.getMchlEndDate())) {
            throw new IllegalArgumentException("Challenge start date cannot be after end date.");
        }
        // 목표 금액이 음수가 아닌지 검사 (DB DDL에 CHECK 제약조건이 있지만, 서비스 단에서 미리 검사)
        if (challenge.getMchlTargetAmount() < 0) {
            throw new IllegalArgumentException("Challenge target amount cannot be negative.");
        }
        
        // 기본값 설정 (엔티티의 @Column(nullable=false)와 일치하도록 보장)
        if (challenge.getMchlReward() == null) {
            challenge.setMchlReward(0L); 
        }

        return challengeRepository.save(challenge);
    }


    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeNotFoundException("Challenge not found with ID: " + challengeId));
    }


    @Transactional(readOnly = true)
    public List<Challenge> getChallengesByMemberId(String memberId) {
        return challengeRepository.findByMember_Mmemid(memberId);
    }


    public Challenge updateChallenge(Challenge updatedChallenge) {
        Challenge existingChallenge = challengeRepository.findById(updatedChallenge.getMchlId())
                .orElseThrow(() -> new ChallengeNotFoundException("Challenge not found with ID: " + updatedChallenge.getMchlId()));

        existingChallenge.setMchlName(updatedChallenge.getMchlName());
        existingChallenge.setMchlTargetAmount(updatedChallenge.getMchlTargetAmount());
        existingChallenge.setMchlStartDate(updatedChallenge.getMchlStartDate());
        existingChallenge.setMchlEndDate(updatedChallenge.getMchlEndDate());
        existingChallenge.setMchlReward(updatedChallenge.getMchlReward());
        existingChallenge.setMchlContents(updatedChallenge.getMchlContents());

        // 유효성 검사
        if (existingChallenge.getMchlStartDate().isAfter(existingChallenge.getMchlEndDate())) {
            throw new IllegalArgumentException("Challenge start date cannot be after end date.");
        }
        if (existingChallenge.getMchlTargetAmount() < 0) {
            throw new IllegalArgumentException("Challenge target amount cannot be negative.");
        }

        return challengeRepository.save(existingChallenge);
    }

    public void deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }
}