package com.MooneyB.Member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.MooneyB.Mail.MailService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MemberController {

	@Autowired
	private MemberDAO mDAO;
	
	@PostMapping(value = "/do.registerpage")
	public String RegisterInfo(
			@RequestParam("id") String id,
			@RequestParam("pw") String pw,
			@RequestParam("nick") String nick,
			@RequestParam("birth") String birth,
			@RequestParam(name = "photoTemp", required = false) MultipartFile photoTemp,
			HttpServletRequest req,
			MemberDTO MDTO
			) {
		String[] memInfo = { id, pw, nick, birth };
		mDAO.getSaveMemberData(MDTO, photoTemp, req, memInfo);
		return "ok";
	}
	
	@PostMapping(value = "/do.Idcheck")
	public Map<String, Boolean> IdCheck(@RequestBody RegIdCheck RIC) {
		Map<String, Boolean> result = new HashMap<>();
		String id = RIC.getRegid();
		System.out.println(RIC.getRegid());
		
		String retuna = null;
		
		retuna = mDAO.RegisterIDCheck(id, retuna);
		
		System.out.println(retuna);
		
		if (retuna.equals("ok")) {
			result.put("idpossible", true);
		}else {
			result.put("idpossible", false);
		}
		
		return result;
	}
	
	@PostMapping(value = "/do.NickCheck")
	public Map<String, Boolean> NickCheck(@RequestBody RegIdCheck RIC) {
		Map<String, Boolean> result = new HashMap<>();
		String nc = RIC.getRegnc();
		System.out.println(RIC.getRegnc());
		
		String retuna = null;
		
		retuna = mDAO.RegisterNCCheck(nc, retuna);
		
		System.out.println(retuna);
		
		if (retuna.equals("ok")) {
			result.put("nickpossible", true);
		}else {
			result.put("nickpossible", false);
		}
		
		return result;
	}

	@PostMapping(value = "/do.login")
	public Map<String, Boolean> RegisterInfo(@RequestBody LoginDTO lDTO, HttpServletRequest req) {
		System.out.println(lDTO.getLoginId() + lDTO.getLoginPw());
		Map<String, Boolean> result = new HashMap<>();
		
		mDAO.login(lDTO, req, result);
		//mDAO.isLogined(req);
	    return result;
	}
	
}
