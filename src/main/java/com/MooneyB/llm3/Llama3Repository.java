package com.MooneyB.llm3;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.MooneyB.Expense.Expense;

public interface Llama3Repository extends CrudRepository<Expense, Long> {
	@Query(value = """
			   SELECT
			    TO_CHAR(TRUNC(SYSDATE, 'IW'), 'YYYY-MM-DD')
			    || ' ~ ' ||
			    TO_CHAR(TRUNC(SYSDATE, 'IW') + 6, 'YYYY-MM-DD') AS 주간범위,
			    TO_CHAR(MEXP_DT, 'YYYY-MM-DD') AS 지출일,
			    MEXP_DEC AS 지출카테고리,
			    MEXP_AMT AS 지출가격
			FROM Mooney_Expense
			WHERE MEXP_MMEM_ID = :memberId
			  AND MEXP_DT BETWEEN TRUNC(SYSDATE, 'IW')
			                  AND TRUNC(SYSDATE, 'IW') + 6 + (23/24) + (59/1440) + (59/86400)
			ORDER BY MEXP_DT
			   """, nativeQuery = true)
	List<String> getWeeklyExpenseSummary(@Param("memberId") String memberId);

	@Query(value = """
			SELECT
				TO_CHAR(TRUNC(SYSDATE, 'MM'), 'YYYY-MM-DD')
				|| ' ~ ' ||
				TO_CHAR(LAST_DAY(SYSDATE), 'YYYY-MM-DD') AS 월간범위,
				TO_CHAR(MEXP_DT, 'YYYY-MM-DD') AS 지출일,
				MEXP_DEC AS 지출카테고리,
				MEXP_AMT AS 지출가격
			FROM Mooney_Expense
			WHERE MEXP_MMEM_ID = :memberId
				AND MEXP_TYPE = 'E'
				AND MEXP_DT BETWEEN TRUNC(SYSDATE, 'MM')
				AND LAST_DAY(SYSDATE) + (23/24) + (59/1440) + (59/86400)
			ORDER BY MEXP_DT
					    """, nativeQuery = true)
	List<String> getMonthlyExpenseSummary(@Param("memberId") String memberId);

	@Query(value = """
				SELECT
			       MEXP_AMT AS 사용금액,
			       MEXP_DEC AS 사용처,
			       MEXP_DT AS 지출일,
			       TO_CHAR(TRUNC(ADD_MONTHS(SYSDATE, -1), 'MM'), 'YYYY-MM-DD') || ' ~ ' ||
			       TO_CHAR(LAST_DAY(ADD_MONTHS(SYSDATE, -1)), 'YYYY-MM-DD') AS 월간범위
			FROM MOONEY_EXPENSE
			WHERE MEXP_TYPE = 'E'
			  AND MEXP_MMEM_ID = :memberId
			  AND EXTRACT(YEAR FROM MEXP_DT) = EXTRACT(YEAR FROM ADD_MONTHS(SYSDATE, -1))
			  AND EXTRACT(MONTH FROM MEXP_DT) = EXTRACT(MONTH FROM ADD_MONTHS(SYSDATE, -1))
					    """, nativeQuery = true)
	List<String> getLateMonthlyExpenseSummary(@Param("memberId") String memberId);

	@Query(value = """
			SELECT MEXP_DT AS 지출일
			, MEXP_AMT AS 지출가격
			, MEXP_DEC AS 지출상세
			, MEXP_RPTDD AS 다음지출일
			, MEXP_FREQUENCY AS 지출주기
			, MEXP_RPT AS 반복지출유무
			FROM MOONEY_EXPENSE
			WHERE MEXP_RPT = 'T' AND MEXP_TYPE='E' AND MEXP_MMEM_ID = :memberId
								    """, nativeQuery = true)
	List<String> getRepeatPriceData(@Param("memberId") String memberId);

	@Query(value = """
			SELECT MEXP_AMT AS 급여,
			       MEXP_DEC AS 금액분류,
			       MEXP_DT AS 급여지급일,
			       MEXP_RPTDD AS 다음급여일
			FROM MOONEY_EXPENSE
			WHERE MEXP_TYPE = 'I'
			  AND MEXP_RPT = 'T'
			  AND MEXP_MMEM_ID = :memberId
			  AND EXTRACT(MONTH FROM MEXP_DT) = EXTRACT(MONTH FROM SYSDATE)
				""", nativeQuery = true)
	List<String> getInspensiveData(@Param("memberId") String memberId);

	@Query(value = """
			SELECT MEXP_DEC, MEXP_DT
			FROM MOONEY_EXPENSE
			WHERE MEXP_TYPE='E' AND MEXP_MMEM_ID = :memberId
														    """, nativeQuery = true)
	List<String> getAMTandDEC(@Param("memberId") String memberId);

	@Query(value = """
					SELECT SUM(COUNT(MEXP_DEC)) AS CNT, SUM(MEXP_AMT)
			FROM MOONEY_EXPENSE
			WHERE MEXP_MMEM_ID = :memberId
			  AND MEXP_TYPE = 'E'
			  AND EXTRACT(MONTH FROM MEXP_DT) =  EXTRACT(MONTH FROM ADD_MONTHS(SYSDATE, -1))
			GROUP BY MEXP_DEC, MEXP_AMT
			ORDER BY CNT DESC
											    """, nativeQuery = true)
	List<String> LateMonthjichulcountAndFullPrice(@Param("memberId") String memberId);

	@Query(value = """
			SELECT SUM(COUNT(MEXP_DEC)) AS CNT, SUM(MEXP_AMT)
			FROM MOONEY_EXPENSE
			WHERE MEXP_MMEM_ID = :memberId
			  AND MEXP_TYPE = 'E'
			  AND EXTRACT(MONTH FROM MEXP_DT) =  EXTRACT(MONTH FROM SYSDATE)
			GROUP BY MEXP_DEC, MEXP_AMT
			ORDER BY CNT DESC
					""", nativeQuery = true)
	List<String> CurrentMonthJichulAndFullPrice(@Param("memberId") String memberId);

	@Query(value = """
						SELECT
			    MEXP_DEC AS 지출내용,
			    COUNT(*) AS 발생횟수,
			    SUM(MEXP_AMT) AS 총금액
			FROM MOONEY_EXPENSE
			WHERE MEXP_MMEM_ID = :memberId
			  AND MEXP_TYPE = 'E'
			  AND EXTRACT(MONTH FROM MEXP_DT) = EXTRACT(MONTH FROM SYSDATE)
			  AND MEXP_AMT >= 100000  -- 또는 500000
			GROUP BY MEXP_DEC
			ORDER BY 총금액 DESC
						""", nativeQuery = true)
	List<String> getHighPriceinfo(@Param("memberId") String meberId);

}
