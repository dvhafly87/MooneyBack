package com.MooneyB.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
	
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegIdCheck {
	
	//회원가입 아이디 중복체크
	private String regid;
	
	//회원가입 닉네임 중복체크
	private String regnc;
	
	//회원가입 이메일 인증처리
	private String regem;
	
	//회원가입 이메일 인증번호
	private String regan;
	
	//로그인 이후 사진 반영을 위한 음 어쩌고 
	private String regpt;
}
