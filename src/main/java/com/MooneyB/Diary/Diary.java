package com.MooneyB.Diary;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.MooneyB.Member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
@AllArgsConstructor  
@Table(name="MOONEY_DIARY")
@SequenceGenerator(
	    name = "MDIA_ID_GENERATOR", // @GeneratedValue의 generator 이름과 일치
	    sequenceName = "MDIA_ID_SEQ", // Oracle DB에 생성할 실제 시퀀스 이름
	    initialValue = 1, // 시퀀스 시작 값
	    allocationSize = 1 // 시퀀스 할당 크기 (1로 설정하여 매번 DB에서 가져옴)
	)
public class Diary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MDIA_ID_GENERATOR") 
	@Column(name = "MDIA_ID")
	private Long mdiaId;
	
	@Column(name = "MDIA_DATE", nullable = false) 
    @Temporal(TemporalType.DATE) 
    private LocalDate mdiaDate;
	
	@Column(name = "MDIA_CONTENT", length = 3000, nullable = false)
	private String mdiaContent;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 (필요할 때만 회원 정보 로드)
    @JoinColumn(name = "MDIA_MMEM_ID", nullable = false) 
	@JsonIgnore
    private Member member;
}
