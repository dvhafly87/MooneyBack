package com.MooneyB.Diary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MooneyB.common.exceptions.*;

import com.MooneyB.Member.Member; // Member 클래스 import 추가
import com.MooneyB.Member.MemberRepo;


@Service
@Transactional
public class DiaryService {

	private final DiaryRepository dr;
	private final MemberRepo mr;

	public DiaryService(DiaryRepository dr, MemberRepo mr) {
		this.dr = dr;
		this.mr = mr;
	}

	public List<Diary> getDiaryList(String memberId){
		return dr.findByMember_Mmemid(memberId);
	}

	public Diary getDiaryById(Long diaryId) {
		return dr.findById(diaryId)
				.orElseThrow(() -> new EntityNotFoundException("Diary not found with ID: " + diaryId));
	}

	
	public Diary createDiary(Diary diary, String memberId) {
		Member member = mr.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

		diary.setMember(member);

		return dr.save(diary);
	}



	public Diary updateDiary(Diary diary) {
		Diary existingDiary = dr.findById(diary.getMdiaId())
				.orElseThrow(() -> new EntityNotFoundException("Diary not found with ID: " + diary.getMdiaId()));

		existingDiary.setMdiaContent(diary.getMdiaContent());
		// existingDiary.setMdiaDate(diary.getMdiaDate()); // 날짜 변경

		return dr.save(existingDiary);
	}


	public Page<Diary> diaryListPaging(Pageable page, String memberId){
		return dr.findByMember_Mmemid(memberId, page);
	}

	public void deleteDiary(Long diaryId) {
		dr.deleteById(diaryId);
	}

}