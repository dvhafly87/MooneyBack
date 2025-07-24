package com.MooneyB.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.MooneyB.common.exceptions.CategoryNotFoundException;
import com.MooneyB.common.exceptions.MemberNotFoundException;

import com.MooneyB.Category.Category;
import com.MooneyB.Category.CategoryRepository;
import com.MooneyB.Member.Member;
import com.MooneyB.Member.MemberRepo;


@Service
@Transactional
public class ExpenseService{
	
	private final ExpenseRepository er;
	private final MemberRepo mr; 
    private final CategoryRepository cr; 

	public ExpenseService(ExpenseRepository expenseRepository, MemberRepo memberRepository, CategoryRepository categoryRepository) {
        this.er = expenseRepository;
        this.mr = memberRepository;
        this.cr = categoryRepository;
    }


	public Expense createExpense(Expense expense, String memberId, Long categoryId) {
        Member member = mr.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));
        expense.setMember(member); 

        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null for creating an expense.");
        }
        Category category = cr.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));
        expense.setCategory(category); 
        
        // 3. 반복 여부에 따른 로직 처리
        if ("T".equals(expense.getMexpRpt())) { // 반복 지출/수입인 경우
            expense.setMexpStatus("COMPLETED"); 
            if (expense.getMexpDt() == null) {
                expense.setMexpDt(LocalDate.now()); // 실제 발생일이 없으면 오늘 날짜로 설정
            }
            Expense savedInitialExpense = er.save(expense);

            // 다음 예정된 인스턴스를 'PENDING' 상태로 생성
            LocalDate nextDueDate = calculateNextOccurrenceDate(expense.getMexpRptdd(), expense.getMexpFrequency());
            if (nextDueDate != null) {
                Expense pendingExpense = new Expense();
                pendingExpense.setMember(member); 
                pendingExpense.setCategory(category); 
                pendingExpense.setMexpDt(null); 
                pendingExpense.setMexpAmt(expense.getMexpAmt());
                pendingExpense.setMexpDec(expense.getMexpDec());
                pendingExpense.setMexpType(expense.getMexpType());
                pendingExpense.setMexpRpt("T"); // 다음 인스턴스도 반복
                pendingExpense.setMexpRptdd(nextDueDate); // 다음 예정일
                pendingExpense.setMexpStatus("PENDING"); // 다음 인스턴스는 'PENDING' 상태
                pendingExpense.setMexpFrequency(expense.getMexpFrequency());

                er.save(pendingExpense);
            }
            return savedInitialExpense; // 첫 번째 (COMPLETED) 인스턴스 반환
        } else { // 일회성 지출/수입인 경우 (mexpRpt가 'F'이거나 null 등)
            expense.setMexpRpt("F"); // 명시적으로 'F' 설정
            expense.setMexpStatus("COMPLETED"); // 항상 'COMPLETED'
            return er.save(expense);
        }
    }

    // ID로 지출/수입 기록 조회
    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(Long expenseId) {
        return er.findById(expenseId);
    }

    // 회원 ID로 모든 지출/수입 기록 조회
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByMemberId(String memberId) {
        return er.findByMember_Mmemid(memberId);
    }

    // 지출/수입 기록 삭제
    public void deleteExpense(Long expenseId) {
        er.deleteById(expenseId);
    }


    private LocalDate calculateNextOccurrenceDate(LocalDate currentDate, String frequency) {
        if (currentDate == null || frequency == null) {
            return null;
        }
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return currentDate.plusDays(1);
            case "WEEKLY":
                return currentDate.plusWeeks(1);
            case "MONTHLY":
                return currentDate.plusMonths(1);
            case "YEARLY":
                return currentDate.plusYears(1);
            default:
                // 알 수 없는 주기는 다음 발생일을 계산하지 않음 (혹은 예외 처리)
                return null;
        }
    }

    

    /**
     * 만료된 반복 지출/수입을 처리하고 다음 인스턴스를 생성.
     * 이 메서드는 스케줄러(예: Spring @Scheduled)에 의해 주기적으로 호출될 수 있음.
     * @param memberId 처리할 회원의 ID (모든 회원을 처리하려면 null 또는 특정 로직 추가)
     * @param currentDate 기준 날짜 (보통 LocalDate.now())
     * @return 처리된(완료된) 반복 지출/수입의 개수
     */
    public int processDueRepeatingExpenses(String memberId, LocalDate currentDate) {
        int processedCount = 0;

        // 1. 특정 회원의, 반복 설정('T')되어 있고, 현재 날짜 이전에 만료 예정일이 있으며, 상태가 'PENDING'인 지출/수입을 조회
        // (memberId가 null이면 모든 회원을 대상으로 조회하도록 로직 확장 가능)
        List<Expense> dueRepeatingExpenses = er.findByMember_MmemidAndMexpRptAndMexpRptddBeforeAndMexpStatus(
                memberId, "T", currentDate, "PENDING");

        for (Expense expense : dueRepeatingExpenses) {
            // 2. 현재 만료된 인스턴스를 'COMPLETED'로 업데이트
            er.updateMexpStatusById(expense.getMexpId(), "COMPLETED");
            processedCount++;

            // 3. 다음 반복 인스턴스 생성
            LocalDate nextDueDate = calculateNextOccurrenceDate(expense.getMexpRptdd(), expense.getMexpFrequency());

            if (nextDueDate != null) {
                Expense nextExpense = new Expense();
                nextExpense.setMember(expense.getMember()); // 연관된 Member 객체 복사
                nextExpense.setMexpDt(null); // 실제 발생일은 아직 없으므로 null
                nextExpense.setMexpAmt(expense.getMexpAmt());
                nextExpense.setMexpDec(expense.getMexpDec());
                nextExpense.setMexpType(expense.getMexpType());
                nextExpense.setMexpRpt("T"); // 다음 인스턴스도 반복
                nextExpense.setMexpRptdd(nextDueDate); // 다음 예정일
                nextExpense.setMexpStatus("PENDING"); // 다음 인스턴스는 'PENDING' 상태
                nextExpense.setMexpFrequency(expense.getMexpFrequency());

                er.save(nextExpense);
            }
        }
        return processedCount;
    }
    
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByCategoryIdAndMemberId(String memberId, Long categoryId) {
        return er.findByCategory_McatIdAndMember_Mmemid(categoryId, memberId);
    }
}
