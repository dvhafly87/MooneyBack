package com.MooneyB.Expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ExpenseRepository extends JpaRepository<Expense, Long>{
	
	List<Expense> findByMember_Mmemid(String memberId);
	
	List<Expense> findByMember_MmemidAndMexpRptAndMexpRptddBeforeAndMexpStatus(
            String mmemid, String mexpRpt, LocalDate mexpRptdd, String mexpStatus);

    @Modifying
    @Query("UPDATE Expense e SET e.mexpStatus = :newStatus WHERE e.mexpId = :mexpId")
    int updateMexpStatusById(@Param("mexpId") Long mexpId, @Param("newStatus") String newStatus);

    List<Expense> findByCategory_McatIdAndMember_Mmemid(Long mcatId, String memberId);
}
