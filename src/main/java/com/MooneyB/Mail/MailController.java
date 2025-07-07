package com.MooneyB.Mail;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {
	@Autowired
	private MailService mailService;

	private int number; // 이메일 인증 숫자를 저장하는 변수

	// 인증 이메일 전송
	@PostMapping("/mailSend")
	public HashMap<String, Object> mailSend(@RequestParam("email") String mail) {
		HashMap<String, Object> map = new HashMap<>();
		System.out.println(mail);

		try {
			number = mailService.sendMail(mail);
			String num = String.valueOf(number);
			map.put("success", Boolean.TRUE);
			map.put("number", num);
		} catch (Exception e) {
			map.put("success", Boolean.FALSE);
			map.put("error", e.getMessage());
		}

		return map;
	}

//	 인증번호 일치여부 확인
	@GetMapping("/mailCheck")
	public ResponseEntity<?> mailCheck(@RequestParam("userNumber") String userNumber) {

		boolean isMatch = userNumber.equals(String.valueOf(number));

		return ResponseEntity.ok(isMatch);
	}
}
