package com.MooneyB.Member;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberFormDTO {
	private String id;
	private String pw;
	private String nick;
	private String birth;
	private MultipartFile photoTemp;
}
