package com.MooneyB;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.MooneyB.Member.MemberDAO;
import com.MooneyB.Member.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
public class HomeController {

	@Autowired
	private MemberDAO mDAO;

	@PostMapping("/test")
	@ResponseBody
	public String test() {
		System.out.println("HI");
		return "test ok";
	}

	@ResponseBody
	@PostMapping("/do.registerpage")
	public ResponseEntity<?> registerUser(@RequestBody MemberDTO MDTO) {
	    return null;
	}
}
