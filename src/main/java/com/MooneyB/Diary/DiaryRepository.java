package com.MooneyB.Diary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long>{
	
	public List<Diary> findByMember_Mmemid(String memberId);
	
	public Page<Diary> findByMember_Mmemid(String memberID, Pageable page);
}
