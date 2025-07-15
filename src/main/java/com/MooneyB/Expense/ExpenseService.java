package com.MooneyB.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ExpenseService{
	
	private final ExpenseRepository er;


	public ExpenseService(ExpenseRepository expenseRepository) {
        this.er = expenseRepository;
    }

    // 새로운 지출/수입 기록 생성 (반복 여부와 관계없이 저장)
    public Expense createExpense(Expense expense) {
        // Expense 엔티티의 mexpStatus는 기본적으로 "COMPLETED"로 설정.
        // 반복 지출의 첫 인스턴스라면 mexpRpt = 'T'와 mexpFrequency, mexpRptdd가 설정됨.
        return er.save(expense);
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

    /**
     * 반복 지출/수입의 다음 발생일을 계산.
     * @param currentDate 기준 날짜 (보통 mexpRptdd)
     * @param frequency 반복 주기 (예: "DAILY", "WEEKLY", "MONTHLY", "YEARLY")
     * @return 다음 발생일 (계산할 수 없으면 null)
     */
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
     * 새로운 반복 지출/수입 계획을 생성.
     * 이 메서드는 첫 번째 인스턴스를 'COMPLETED'로 저장하고, 다음 예정된 인스턴스를 'PENDING'으로 생성.
     * @param initialExpense 반복 설정된 Expense 객체.
     * 필수: member, mexpAmt, mexpDec, mexpType, mexpRpt='T', mexpRptdd(첫 예정일), mexpFrequency.
     * @return 생성된 첫 번째 Expense 객체
     */
    public Expense createNewRepeatingExpense(Expense initialExpense) {
        // 1. 첫 번째 인스턴스 (현재 발생한 지출/수입)를 'COMPLETED' 상태로 저장
        initialExpense.setMexpRpt("T"); // 반복 지출임을 명시
        initialExpense.setMexpStatus("COMPLETED"); // 첫 인스턴스는 완료 상태
        if (initialExpense.getMexpDt() == null) {
            initialExpense.setMexpDt(LocalDate.now()); // 실제 발생일이 없으면 오늘 날짜로 설정
        }
        Expense savedInitialExpense = er.save(initialExpense);

        // 2. 다음 예정된 인스턴스를 'PENDING' 상태로 생성
        LocalDate nextDueDate = calculateNextOccurrenceDate(initialExpense.getMexpRptdd(), initialExpense.getMexpFrequency());
        if (nextDueDate != null) {
            Expense pendingExpense = new Expense();
            pendingExpense.setMember(initialExpense.getMember());
            pendingExpense.setMexpDt(null); // 예정된 지출이므로 실제 발생일은 null
            pendingExpense.setMexpAmt(initialExpense.getMexpAmt());
            pendingExpense.setMexpDec(initialExpense.getMexpDec());
            pendingExpense.setMexpType(initialExpense.getMexpType());
            pendingExpense.setMexpRpt("T"); // 다음 인스턴스도 반복
            pendingExpense.setMexpRptdd(nextDueDate); // 다음 예정일
            pendingExpense.setMexpStatus("PENDING"); // 다음 인스턴스는 'PENDING' 상태
            pendingExpense.setMexpFrequency(initialExpense.getMexpFrequency());

            er.save(pendingExpense);
        }
        return savedInitialExpense;
    }

    /**
     * 만료된 반복 지출/수입을 처리하고 다음 인스턴스를 생성합니다.
     * 이 메서드는 스케줄러(예: Spring @Scheduled)에 의해 주기적으로 호출될 수 있습니다.
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
}
