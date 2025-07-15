package com.MooneyB.Expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface ExpenseRepository extends CrudRepository<Expense, Long>{
	// 회원 ID로 지출/수입 기록 조회
	List<Expense> findByMember_Mmemid(String memberId);
	
	// 특정 회원의 반복 지출/수입 중, 예정일이 현재 날짜 이전이면서 'PENDING' 상태인 기록 조회
    List<Expense> findByMember_MmemidAndMexpRptAndMexpRptddBeforeAndMexpStatus(
            String mmemid, String mexpRpt, LocalDate mexpRptdd, String mexpStatus);

    // MEXP_ID로 지출/수입 상태 업데이트
    @Modifying
    @Query("UPDATE Expense e SET e.mexpStatus = :newStatus WHERE e.mexpId = :mexpId")
    int updateMexpStatusById(@Param("mexpId") Long mexpId, @Param("newStatus") String newStatus);

}
