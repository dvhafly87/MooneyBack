package com.MooneyB.Challenge;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>{
	
	public List<Challenge> findByMember_Mmemid(String memberId);
}
