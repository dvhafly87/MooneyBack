//package com.MooneyB.Expense;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//import java.time.LocalDate;
//import java.util.Date;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
////import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
////import org.springframework.boot.autoconfigure.domain.EntityScan;
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
//class MooneyBackExpense {
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
//	@BeforeEach
//	void setUp() {
//		
//		Optional<Member> d = mr.findById("uiuiui234");
//        if (d.isPresent()) {
//        	m = d.get();
//        } else {
//        	throw new NoSuchElementException("misio234을 찾을 수 없었습니다.");
//        }
//        
//        List<Expense> existingExp = er.findByMember_Mmemid(m.getMmemid());
//        er.deleteAll(existingExp);
//    }
//	
//	@AfterEach // 각 테스트 메서드 실행 후 정리 작업을 수행합니다.
//    void tearDown() {
//        // 현재 테스트에서 'm'가 생성한 알림만 삭제합니다.
//        // (m 자체는 삭제하지 않습니다.)
//        List<Expense> expenseToDelete = er.findByMember_Mmemid(m.getMmemid());
//        er.deleteAll(expenseToDelete);
//    }
//
//
//	@Test
//    @DisplayName("새로운 지출 기록 저장 및 ID로 조회 테스트")
//    void testSaveAndFindExpenseById() {
//        // Given
//        Expense expense = new Expense();
//        expense.setMember(m);
//        expense.setMexpDt(LocalDate.of(2025, 7, 5));
//        expense.setMexpAmt(15000L);
//        expense.setMexpDec("점심 식사"); // 이 Expense는 이 테스트에서만 저장됩니다.
//        expense.setMexpType("E");
//        expense.setMexpRpt("F");
//        expense.setMexpRptdd(LocalDate.of(2025, 7, 5));
//
//        Expense savedExpense = er.save(expense);
//
//        assertThat(savedExpense).isNotNull();
//        assertThat(savedExpense.getMexpId()).isNotNull();
//        assertThat(savedExpense.getMexpDec()).isEqualTo("점심 식사");
//
//        Optional<Expense> foundExpense = er.findById(savedExpense.getMexpId());
//        assertThat(foundExpense).isPresent();
//        assertThat(foundExpense.get().getMexpDec()).isEqualTo("점심 식사");
//        assertThat(foundExpense.get().getMember().getMmemid()).isEqualTo("uiuiui234");
//    }
//
//    @Test
//    @DisplayName("회원 ID로 지출 기록 목록 조회 테스트")
//    void testFindExpensesByMemberId() {
//        // Given
//        // 이 테스트에 필요한 Expense만 여기서 저장합니다.
//        Expense expense1 = new Expense();
//        expense1.setMember(m);
//        expense1.setMexpDt(LocalDate.of(2025, 7, 1));
//        expense1.setMexpAmt(5000L);
//        expense1.setMexpDec("커피");
//        expense1.setMexpType("E");
//        expense1.setMexpRpt("F");
//        expense1.setMexpRptdd(LocalDate.of(2025, 7, 1));
//        er.save(expense1);
//
//        Expense expense2 = new Expense();
//        expense2.setMember(m);
//        expense2.setMexpDt(LocalDate.of(2025, 7, 2));
//        expense2.setMexpAmt(20000L);
//        expense2.setMexpDec("저녁 식사");
//        expense2.setMexpType("E");
//        expense2.setMexpRpt("F");
//        expense2.setMexpRptdd(LocalDate.of(2025, 7, 2));
//        er.save(expense2);
//
//        // When
//        List<Expense> expenses = er.findByMember_Mmemid(m.getMmemid());
//
//        // Then
//        assertThat(expenses).hasSize(2);
//        // 순서가 보장되지 않을 수 있으므로, 내용으로 검증하는 것이 더 안전합니다.
//        assertThat(expenses).extracting(Expense::getMexpDec).containsExactlyInAnyOrder("커피", "저녁 식사");
//    }
//
//    @Test
//    @DisplayName("지출 기록 삭제 테스트")
//    void testDeleteExpense() {
//        // Given
//        Expense expenseToDelete = new Expense();
//        expenseToDelete.setMember(m);
//        expenseToDelete.setMexpDt(LocalDate.now());
//        expenseToDelete.setMexpAmt(1000L);
//        expenseToDelete.setMexpDec("삭제될 지출");
//        expenseToDelete.setMexpType("E");
//        expenseToDelete.setMexpRpt("F");
//        expenseToDelete.setMexpRptdd(LocalDate.now());
//        Expense savedExpense = er.save(expenseToDelete);
//
//        // When
//        er.deleteById(savedExpense.getMexpId());
//
//        // Then
//        Optional<Expense> deletedExpense = er.findById(savedExpense.getMexpId());
//        assertThat(deletedExpense).isNotPresent();
//    }
//}
