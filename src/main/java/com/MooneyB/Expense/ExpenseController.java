package com.MooneyB.Expense;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/expenses") 
public class ExpenseController {

    private final ExpenseService expenseService;

   
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }


    // 💰 지출/수입 기록 생성

    @PostMapping("/member/{memberId}") // POST /expenses/member/{memberId}
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense, @PathVariable("memberId") String memberId, @RequestParam("mcatId") Long mcatId) {
        Expense createdExpense = expenseService.createExpense(expense, memberId, mcatId);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }


   // 🔍 ID로 지출/수입 기록 조회

    @GetMapping("/{mexpId}") // GET /expenses/{mexpId}
    public ResponseEntity<Expense> getExpenseById(@PathVariable("mexpId") Long mexpId) {
        Optional<Expense> expense = expenseService.getExpenseById(mexpId);
        return expense.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    // 👤 회원 ID로 모든 지출/수입 기록 조회

    @GetMapping("/member/{memberId}") // GET /expenses/member/{memberId}
    public ResponseEntity<List<Expense>> getExpensesByMemberId(@PathVariable("memberId") String memberId) {
        List<Expense> expenses = expenseService.getExpensesByMemberId(memberId);
        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK); // 200 OK
    }

   

    // 🗓️ 날짜 범위로 지출/수입 기록 조회 (추가된 기능)

   @GetMapping("/member/{memberId}/by-date-range") // GET /expenses/member/{memberId}/by-date-range
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @PathVariable("memberId") String memberId,
            @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Expense> allExpenses = expenseService.getExpensesByMemberId(memberId);
        List<Expense> filteredExpenses = allExpenses.stream()
            .filter(expense -> {
                LocalDate expenseDate = expense.getMexpDt();

                return expenseDate != null && 
                       !expenseDate.isBefore(startDate) && 
                       !expenseDate.isAfter(endDate);
            })
            .collect(Collectors.toList());

        if (filteredExpenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(filteredExpenses, HttpStatus.OK);
    }



//    @GetMapping("/member/{memberId}/paged")
//    public ResponseEntity<Page<Expense>> getExpensesByMemberIdPaged(
//            @PathVariable("memberId") String memberId,
//            @RequestParam(defaultValue = "0") int page, // 현재 페이지 (0부터 시작)
//            @RequestParam(defaultValue = "10") int size, // 한 페이지당 항목 수
//            @RequestParam(defaultValue = "mexpDt,desc") String[] sort // 정렬 기준 (예: "mexpDt,desc" -> 날짜 내림차순)
//    ) {
//        Sort.Direction direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
//        List<Expense> allExpenses = expenseService.getExpensesByMemberId(memberId);
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), allExpenses.size());
//        Page<Expense> expensePage = new org.springframework.data.domain.PageImpl<>(
//            allExpenses.subList(start, end), pageable, allExpenses.size());
//        
//        if (expensePage.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(expensePage, HttpStatus.OK);
//    }


    // 🗑️ 지출/수입 기록 삭제

    @DeleteMapping("/{mexpId}") // DELETE /expenses/{mexpId}
    public ResponseEntity<Void> deleteExpense(@PathVariable("mexpId") Long mexpId) {
        expenseService.deleteExpense(mexpId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }


    // ⚙️ 만료된 반복 지출/수입 처리 (관리자 또는 스케줄러용)

    @PostMapping("/process-due")
    public ResponseEntity<String> processDueRepeatingExpenses(
            @RequestParam(value = "memberId", required = false) String memberId,
            @RequestParam(value = "currentDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate currentDate) {
        
        // currentDate가 제공되지 않으면 오늘 날짜를 사용합니다.
        LocalDate processDate = (currentDate != null) ? currentDate : LocalDate.now();
        
        int processedCount = expenseService.processDueRepeatingExpenses(memberId, processDate);
        return new ResponseEntity<>("Processed " + processedCount + " due repeating expenses.", HttpStatus.OK);
    }
}