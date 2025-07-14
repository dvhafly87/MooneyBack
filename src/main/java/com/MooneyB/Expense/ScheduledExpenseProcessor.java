package com.MooneyB.Expense; // 적절한 패키지명으로 변경하세요.

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // 모든 멤버를 조회하는 경우 필요
import com.MooneyB.Member.MemberRepo; // 모든 멤버를 조회하는 경우 필요
import com.MooneyB.Member.Member; // 모든 멤버를 조회하는 경우 필요

@Component // 스프링 빈으로 등록
public class ScheduledExpenseProcessor {

    private final ExpenseService expenseService;
    private final MemberRepo memberRepo; // 모든 멤버를 조회하기 위해 MemberRepo 주입

    public ScheduledExpenseProcessor(ExpenseService expenseService, MemberRepo memberRepo) {
        this.expenseService = expenseService;
        this.memberRepo = memberRepo;
    }

    /**
     * 매일 자정에 반복 지출/수입을 처리합니다.
     * cron 표현식: "초 분 시 일 월 요일"
     * "0 0 0 * * ?"는 매일 0시 0분 0초를 의미합니다.
     * 실제 서비스에서는 서버 시간대, 트랜잭션 충돌, 예외 처리 등을 고려하여 더 견고하게 구현해야 합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void processDailyRepeatingExpenses() {
        System.out.println("반복 지출/수입 처리 스케줄러 실행: " + LocalDateTime.now());

        // 모든 회원의 반복 지출/수입을 처리하는 로직
        List<Member> allMembers = (List<Member>) memberRepo.findAll(); // 모든 멤버 조회
        int totalProcessedCount = 0;

        for (Member member : allMembers) {
            try {
                int processedCount = expenseService.processDueRepeatingExpenses(member.getMmemid(), LocalDate.now());
                totalProcessedCount += processedCount;
                System.out.println("  회원 [" + member.getMmemid() + "] 처리된 반복 지출/수입 개수: " + processedCount);
            } catch (Exception e) {
                System.err.println("  회원 [" + member.getMmemid() + "] 반복 지출/수입 처리 중 오류 발생: " + e.getMessage());
                // 오류 로깅 및 알림 처리 (예: 슬랙 알림, 이메일 등)
            }
        }
        System.out.println("총 처리된 반복 지출/수입 개수: " + totalProcessedCount);
    }

    // 테스트를 위해 짧은 주기로 실행하는 스케줄러 (개발/테스트 환경에서만 사용)
    // @Scheduled(fixedRate = 60000) // 1분(60초)마다 실행 (밀리초 단위)
    // public void processRepeatingExpensesForTesting() {
    //     System.out.println("테스트용 반복 지출/수입 스케줄러 실행: " + LocalDateTime.now());
    //     String testMemberId = "uiuiui234"; // 테스트용 회원 ID
    //     int processedCount = expenseService.processDueRepeatingExpenses(testMemberId, LocalDate.now());
    //     System.out.println("  테스트용 회원 [" + testMemberId + "] 처리된 반복 지출/수입 개수: " + processedCount);
    // }
}
