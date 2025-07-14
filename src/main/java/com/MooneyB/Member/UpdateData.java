package com.MooneyB.Member;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateData {
	private MultipartFile ephoto;
	private String ecurpw;
	private String epwString;
	private String enick;
	private String eid;
}
