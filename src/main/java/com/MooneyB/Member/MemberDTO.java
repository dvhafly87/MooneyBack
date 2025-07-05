package com.MooneyB.Member;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "Mooney_Member")
public class MemberDTO {
	
	@Id
	@Column(name = "MMEM_ID")
	private String id;

	@Column(name = "MMEM_PW")
	private String pw;
	
	@Column(name = "MMEM_PPHOTO")
	private String pphoto;

	@Column(name = "MMEM_NICK")
	private String nick;
	
	@Column(name = "MMEM_BIR")
	private Date bir;
	
	@Column(name = "MMEM_REGD")
	@CreationTimestamp
	private Date mmemrgd;
	
	@Column(name = "MMEM_PNT")
	private Integer mmempnt;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getPphoto() {
		return pphoto;
	}

	public void setPphoto(String pphoto) {
		this.pphoto = pphoto;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Date getBir() {
		return bir;	
	}

	public void setBir(Date bir) {
		this.bir = bir;
	}

	public Date getMmemrgd() {
		return mmemrgd;
	}

	public void setMmemrgd(Date mmemrgd) {
		this.mmemrgd = mmemrgd;
	}

	public Integer getMmempnt() {
		return mmempnt;
	}

	public void setMmempnt(Integer mmempnt) {
		this.mmempnt = mmempnt;
	}
}
