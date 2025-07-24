package com.MooneyB.Expense;


import java.time.LocalDate;

import com.MooneyB.Category.Category;
import com.MooneyB.Member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name= "MOONEY_EXPENSE")
@SequenceGenerator(
        name = "EXPENSE_SEQ_GENERATOR", 
        sequenceName = "MOONEY_EXPENSE_SEQ", 
        initialValue = 1, // 시퀀스 시작 값
        allocationSize = 1 // 시퀀스 할당 크기
    )
public class Expense {
	@Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXPENSE_SEQ_GENERATOR") 
	@Column(name = "MEXP_ID")
	private Long mexpId;


	
	@Column(name = "MEXP_DT", nullable = false)
	private LocalDate mexpDt; 

	@Column(name = "MEXP_AMT", nullable = false)
	private Long mexpAmt; 

	@Column(name = "MEXP_DEC", nullable = false, length = 3000)
	private String mexpDec; 

	@Column(name = "MEXP_TYPE", nullable = false, length = 1)
	private String mexpType; 

	@Column(name = "MEXP_RPT", nullable = false, length = 1)
	private String mexpRpt; 

	@Column(name = "MEXP_RPTDD") 
	private LocalDate mexpRptdd;

    @Column(name = "MEXP_STATUS", nullable = false, length = 20) 
    private String mexpStatus = "COMPLETED"; 
    
    @Column(name = "MEXP_FREQUENCY", length = 10)
    private String mexpFrequency;
    
	@ManyToOne
	@JoinColumn(name = "MEXP_MMEM_ID", referencedColumnName = "MMEM_ID", nullable = false)
	private Member member; 
	
	// --- Category 엔티티와 Many-to-One 관계 추가 ---
    @ManyToOne // Expense (다) -> Category (일) 관계
    @JoinColumn(name = "MEXP_MCAT_ID", referencedColumnName = "MCAT_ID", nullable = false) // NOT NULL로 변경
    private Category category;
	
}
