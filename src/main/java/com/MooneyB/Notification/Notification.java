package com.MooneyB.Notification;


import java.time.LocalDateTime;

import com.MooneyB.Member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor  
@Entity
@Table(name = "MOONEY_NOTIFICATION") // 매핑될 테이블 이름을 명시
@SequenceGenerator(
    name = "MNOF_ID_GENERATOR", // @GeneratedValue의 generator 이름과 일치
    sequenceName = "MNOF_ID_SEQ", // Oracle DB에 생성할 실제 시퀀스 이름
    initialValue = 1, // 시퀀스 시작 값
    allocationSize = 1 // 시퀀스 할당 크기 (1로 설정하여 매번 DB에서 가져옴)
)
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MNOF_ID_GENERATOR") 
	@Column(name = "MNOF_ID")
	private Long mnofId;
	
	@Column(name = "MNOF_CONTENT", nullable = false, length = 1000)
	private String mnofContent;
	
	@Column(name = "MNOF_TYPE", nullable = false, length = 20)
	private String mnofType;
	
	@Column(name = "MNOF_DT", nullable = false)
	private LocalDateTime mnofDt;
	
	@Column(name = "MNOF_READ_YN", nullable = false, length = 1)
    @Convert(converter = BooleanToYNConverter.class)
	private boolean mnofReadYn = false;
	
	@ManyToOne
	@JoinColumn(name = "MNOF_MMEM_ID", referencedColumnName = "MMEM_ID", nullable = false)
	@JsonIgnore
	private Member member; 

}
