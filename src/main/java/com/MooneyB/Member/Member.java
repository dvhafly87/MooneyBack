package com.MooneyB.Member;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Mooney_Member")
public class Member {
	
	@Id
	@Column(name = "MMEM_ID")
	private String mmemid;

	@Column(name = "MMEM_PW")
	private String mmempw;
	
	@Column(name = "MMEM_PPHOTO")
	private String mmempphoto;

	@Column(name = "MMEM_NICK")
	private String mmemnick;
	
	@Column(name = "MMEM_BIR")
	private Date mmembir;
	
	@Column(name = "MMEM_REGD")
	@CreationTimestamp
	private Date mmemregd;
	
	@Column(name = "MMEM_PNT")
	private Integer mmemppnt;
}
