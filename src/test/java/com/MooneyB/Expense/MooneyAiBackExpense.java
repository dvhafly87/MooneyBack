//package com.MooneyB.Expense;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.Random;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import com.MooneyB.Member.Member;
//import com.MooneyB.Member.MemberRepo;
//
//import jakarta.transaction.Transactional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class MooneyAiBackExpense {
//
//	@Autowired
//	private ExpenseRepository er;
//
//	@Autowired 
//	private MemberRepo mr;
// 
//
//	private Member m;
//	
//	private Random random = new Random();
//	
//	
//	private Expense createAndSaveExpense(Member member, LocalDate date, Long amount, String description, String type, String repeat, LocalDate repeatDate, String status, String frequency) {
//        Expense expense = new Expense();
//        expense.setMexpDt(date);
//        expense.setMexpAmt(amount);
//        expense.setMexpDec(description);
//        expense.setMexpType(type); // "I" (수입) 또는 "E" (지출)
//        expense.setMexpRpt(repeat); // "T" (반복) 또는 "F" (단건)
//        expense.setMexpRptdd(repeatDate); // 반복 지정일 (예: 월급일, 카드결제일)
//        expense.setMexpStatus(status); // 예: "COMPLETED", "PENDING"
//        expense.setMexpFrequency(frequency); // 예: "DAILY", "WEEKLY", "MONTHLY"
//        expense.setMember(member);
//        return er.save(expense);
//    }
//	
//	@BeforeEach
//	void setUp() {
//		Optional<Member> d = mr.findById("hhhh234");
//		if (d.isPresent()) {
//        	m = d.get();
//        } else {
//        	throw new NoSuchElementException("uiuiui234을 찾을 수 없었습니다.");
//        }
//		
//		for (int i = 1; i <= 30; i++) { // 6월 1일부터 30일까지
//            LocalDate currentDate = LocalDate.of(2025, 6, i);
//
//            // 1. 식비 (점심/저녁) - 매일
//            createAndSaveExpense(m, currentDate,
//                    (long) (random.nextInt(10) + 1) * 10000, // 10,000 ~ 100,000원
//                    "점심 식사 (식비)", "E", "F", null, "COMPLETED", null);
//            if (random.nextBoolean()) { // 50% 확률로 저녁 식사
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(15) + 5) * 10000, // 50,000 ~ 200,000원
//                        "저녁 회식 (식비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 2. 교통비 - 평일 (월-금)에 90% 확률로 발생
//            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY && random.nextDouble() < 0.9) {
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(5) + 1) * 1000, // 1,000 ~ 5,000원
//                        "버스/지하철 (교통비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 3. 유흥비 - 주말 (토/일)에 60% 확률로 발생
//            if ((currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) && random.nextDouble() < 0.6) {
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(10) + 3) * 50000, // 150,000 ~ 650,000원
//                        "영화/공연 관람 (유흥비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 4. 의류비/쇼핑 - 주 1회 정도 (랜덤하게)
//            if (random.nextInt(7) == 0) { // 약 1/7 확률
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(20) + 5) * 10000, // 50,000 ~ 250,000원
//                        "의류 구매 (의류비)", "E", "F", null, "COMPLETED", null);
//            }
//        }
//
//        // 7월 데이터 생성 (7월 1일부터 7월 19일 - 오늘 날짜)
//        for (int i = 1; i <= 19; i++) {
//            LocalDate currentDate = LocalDate.of(2025, 7, i);
//
//            // 1. 식비 (간식/커피) - 매일 80% 확률
//            if (random.nextDouble() < 0.8) {
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(5) + 1) * 1000, // 1,000 ~ 5,000원
//                        "간식/커피 (식비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 2. 교통비 - 평일 (월-금)에 100% 발생 (출퇴근)
//            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
//                 createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(2) + 1) * 1250, // 1,250 ~ 2,500원
//                        "출퇴근 (교통비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 3. 유흥비 - 주중에도 가끔 발생 (20% 확률)
//            if (random.nextDouble() < 0.2) {
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(5) + 1) * 20000, // 20,000 ~ 120,000원
//                        "친구와 술 한 잔 (유흥비)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 4. 의류비/쇼핑 - 주말에 한 번 (랜덤하게)
//            if ((currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) && random.nextInt(3) == 0) { // 약 1/3 확률
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(10) + 3) * 10000, // 30,000 ~ 130,000원
//                        "온라인 쇼핑 (의류/잡화)", "E", "F", null, "COMPLETED", null);
//            }
//
//            // 5. 경조사비 (지출, 단건, 가끔)
//            if (random.nextInt(10) == 0) { // 약 1/10 확률
//                createAndSaveExpense(m, currentDate,
//                        (long) (random.nextInt(5) + 5) * 10000, // 50,000 ~ 100,000원
//                        "결혼식 축의금 (경조사비)", "E", "F", null, "COMPLETED", null);
//            }
//        }
//
//        // --- 고정 수입/지출 (반복 내역 포함) ---
//        // 6월
//        createAndSaveExpense(m, LocalDate.of(2025, 6, 25), 2500000L, "6월 급여 (수입)", "I", "T", LocalDate.of(2025,6,25), "COMPLETED", "MONTHLY");
//        createAndSaveExpense(m, LocalDate.of(2025, 6, 25), 700000L, "6월 월세 (주거비)", "E", "T", LocalDate.of(2025,6,25), "COMPLETED", "MONTHLY");
//        createAndSaveExpense(m, LocalDate.of(2025, 6, 28), 150000L, "6월 카드 대금 (금융비)", "E", "F", null, "COMPLETED", null);
//        createAndSaveExpense(m, LocalDate.of(2025, 6, 30), 80000L, "6월 통신비 (생활비)", "E", "T", LocalDate.of(2025,6,30), "COMPLETED", "MONTHLY");
//        createAndSaveExpense(m, LocalDate.of(2025, 6, 1), 30000L, "정기 저축 (저축)", "E", "T", LocalDate.of(2025,6,1), "COMPLETED", "MONTHLY");
//
//
//        // 7월
//        createAndSaveExpense(m, LocalDate.of(2025, 7, 1), 2500000L, "7월 급여 (수입)", "I", "T", LocalDate.of(2025,7,1), "COMPLETED", "MONTHLY");
//        createAndSaveExpense(m, LocalDate.of(2025, 7, 5), 100000L, "부수입 (수입)", "I", "F", null, "COMPLETED", null);
//        createAndSaveExpense(m, LocalDate.of(2025, 7, 15), 700000L, "7월 월세 (주거비)", "E", "T", LocalDate.of(2025,7,25), "PENDING", "MONTHLY"); // 상태 PENDING
//        createAndSaveExpense(m, LocalDate.of(2025, 7, 18), 120000L, "병원비 (의료비)", "E", "F", null, "COMPLETED", null);
//        createAndSaveExpense(m, LocalDate.of(2025, 7, 1), 30000L, "정기 저축 (저축)", "E", "T", LocalDate.of(2025,7,1), "COMPLETED", "MONTHLY");
//
//        // 다른 멤버의 데이터 (테스트 멤버 데이터와 섞이지 않도록)
//        
//        System.out.println("---- Setup Complete. Total Expenses for " + m.getMmemid() + ": " + er.findByMember_Mmemid(m.getMmemid()).size());
////        System.out.println("---- Total Expenses for " + anotherMember.getMmemid() + ": " + expenseRepository.findByMember_Mmemid(anotherMember.getMmemid()).size());
//    }
//	
//	 @Test
//	    @DisplayName("생성된 6월과 7월 데이터 전체 조회 검증")
//	    void testGetAllExpensesForUser() {
//	        // Given (setUp에서 데이터 생성 완료)
//
//	        // When
//	        List<Expense> expenses = er.findByMember_Mmemid(m.getMmemid());
//
//	        // Then
//	        assertNotNull(expenses);
//	        System.out.println("Actual expenses size for testMember: " + expenses.size());
//	        // assertEquals(예상_총_건수, expenses.size()); // setUp 로직 실행 후 이 값을 조정하세요.
//
//	        // 7월 급여가 제대로 들어갔는지 검증 (특정 고정 내역 확인)
//	        assertTrue(expenses.stream().anyMatch(e ->
//	                e.getMexpDt().equals(LocalDate.of(2025, 7, 1)) &&
//	                e.getMexpType().equals("I") &&
//	                e.getMexpAmt().equals(2500000L) &&
//	                e.getMexpDec().equals("7월 급여 (수입)")
//	        ));
//	    }
//}
