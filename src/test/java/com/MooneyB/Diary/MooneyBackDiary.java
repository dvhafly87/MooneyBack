//package com.MooneyB.Diary;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import com.MooneyB.Member.Member;
//import com.MooneyB.Member.MemberRepo;
//import com.MooneyB.Member.MemberService;
//
//import jakarta.transaction.Transactional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class MooneyBackDiary {
//	
//	@Autowired
//	private DiaryService ds;
//	
//	@Autowired
//	private DiaryRepository dr;
//	
//	@Autowired
//	private MemberRepo mr;
//	
//	@Autowired
//	private MemberService ms;
//	
//	private Member m;
//	private Member m2;
//	
//	@BeforeEach
//	void setUp() {
//		
//		Optional<Member> d = mr.findById("uiuiui234");
//		Optional<Member> d2 = mr.findById("hhh234");
//        if (d.isPresent()) {
//        	m = d.get();
//        	m2 = d2.get();
//        } else {
//        	throw new NoSuchElementException("uiuiui234을 찾을 수 없었습니다.");
//        }
//        
//        List<Diary> existingDiary = ds.getDiaryList(m.getMmemid());
//        dr.deleteAll(existingDiary);
//    }
//	
//	@AfterEach // 각 테스트 메서드 실행 후 정리 작업을 수행합니다.
//    void tearDown() {
//        // 현재 테스트에서 'm'가 생성한 알림만 삭제합니다.
//        // (m 자체는 삭제하지 않습니다.)
//        List<Diary> notificationsToDelete = ds.getDiaryList(m.getMmemid());
//        dr.deleteAll(notificationsToDelete);
//    }
//	
//	
//	@Test
//	@DisplayName("리스트 테스트")
//	void testGetList() {
//		ds.createDiary(createTestDiary(m, "용산가서 딤섬먹음"), m.getMmemid());
//		ds.createDiary(createTestDiary(m, "집 갈래"), m.getMmemid());
//		ds.createDiary(createTestDiary(m, "월급날"), m.getMmemid());
//		ds.createDiary(createTestDiary(m, "음 뭐 적지"), m.getMmemid());
//		
//		ds.getDiaryList(m.getMmemid()).toString();
//	}
//	
//	private Diary createTestDiary(Member member, String content) {
//		Diary diary = new Diary();
//		diary.setMdiaDate(LocalDate.now());
//		diary.setMdiaContent(content);
//		diary.setMember(member);
//		return diary;
//	}
//
//}
