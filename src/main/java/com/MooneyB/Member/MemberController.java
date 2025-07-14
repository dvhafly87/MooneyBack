package com.MooneyB.Member;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MemberController {

	@Autowired
	private BCryptPasswordEncoder bcpe;

	@Autowired
	private MemberService mDAO;

	@Value("${mem.member.image}")
	private String memberFolder;

	@PostMapping(value = "/do.registerpage")
	public String RegisterInfo(@RequestParam("id") String id, @RequestParam("pw") String pw,
			@RequestParam("nick") String nick, @RequestParam("birth") String birth,
			@RequestParam(name = "photoTemp", required = false) MultipartFile photoTemp, HttpServletRequest req,
			Member MDTO) {
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
		} else {
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
		} else {
			result.put("nickpossible", false);
		}

		return result;
	}

	@PostMapping(value = "/do.login")
	public ResponseEntity<?> Login(@RequestBody LoginDTO lDTO, HttpServletRequest req) {
		Map<String, Object> result = new HashMap<>();

		// 로그인 검증
		boolean loginSuccess = mDAO.login(lDTO, req, result);

		if (loginSuccess) {
			// 세션에서 로그인된 사용자 정보 가져오기
			Member loginMember = (Member) req.getSession().getAttribute("loginMember");
			LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");

			if (loginMember != null && loginToken != null) {
				// 세션 타임아웃 설정 (30분)
				req.getSession().setMaxInactiveInterval(60 * 60);

				Map<String, Object> tokenData = new HashMap<>();
				tokenData.put("loginId", loginToken.getLoginId());
				tokenData.put("sessionId", req.getSession().getId());
				tokenData.put("loginTime", System.currentTimeMillis());

				return ResponseEntity.ok().body(
						Map.of("isLogined", true, "token", tokenData, "userInfo", Map.of("id", loginMember.getMmemid(),
								"nick", loginMember.getMmemnick(), "point", loginMember.getMmemppnt())));
			}
		}

		return ResponseEntity.ok().body(Map.of("isLogined", false));
	}

	@PostMapping(value = "/do.logincheck")
	public ResponseEntity<?> CheckLoginStatus(@RequestBody RegIdCheck RIC, HttpServletRequest req) {
		Map<String, Object> result = new HashMap<>();

		// 세션에서 로그인 정보 확인
		Member loginMember = (Member) req.getSession().getAttribute("loginMember");
		LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");

		if (loginMember != null && loginToken != null) {
			// 요청된 ID와 세션의 ID가 일치하는지 확인
			if (loginToken.getLoginId().equals(RIC.getRegid())) {
				result.put("isLogined", true);
				result.put("userInfo", Map.of("id", loginMember.getMmemid(), "nick", loginMember.getMmemnick(), "point",
						loginMember.getMmemppnt()));
				result.put("sessionValid", true);
				System.out.println("세션 연장됨");
				req.getSession().setMaxInactiveInterval(60 * 60);
			} else {
				result.put("isLogined", false);
				result.put("sessionValid", false);
			}
		} else {
			result.put("isLogined", false);
			result.put("sessionValid", false);
		}

		return ResponseEntity.ok().body(result);
	}

	@PostMapping(value = "/do.logout")
	public ResponseEntity<?> Logout(HttpServletRequest req) {
		Map<String, Object> result = new HashMap<>();

		try {
			// 세션 무효화 시킴
			req.getSession().invalidate();
			result.put("logoutSuccess", true);
		} catch (Exception e) {
			result.put("logoutSuccess", false);
			e.printStackTrace();
		}

		return ResponseEntity.ok().body(result);
	}

	@PostMapping(value = "/do.MeminfoCheck")
	public ResponseEntity<?> meminfo(HttpServletRequest req, @RequestBody RegIdCheck RIC) {
		Map<String, Object> meminfoMap = new HashMap<>();
		Member loginMember = (Member) req.getSession().getAttribute("loginMember");
		LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");
		System.out.println(loginMember);
		System.out.println(loginToken);
		if (loginToken.getLoginId().equals(RIC.getRegid())) {
			System.out.println("매핑 실행");
			meminfoMap.put("id", loginMember.getMmemid());
			meminfoMap.put("pw", loginMember.getMmempw());
			meminfoMap.put("bir", loginMember.getMmembir());
			meminfoMap.put("nick", loginMember.getMmemnick());
			meminfoMap.put("pphoto", loginMember.getMmempphoto());
			meminfoMap.put("regd", loginMember.getMmemregd());
			meminfoMap.put("ppnt", loginMember.getMmemppnt());
			return ResponseEntity.ok().body(Map.of("Meminfo", meminfoMap));
		}

		return ResponseEntity.ok().body(Map.of("Meminfo", "nothing"));
	}

	@GetMapping("/member.photo/{Memphoto}")
	public ResponseEntity<Resource> imageGet(@PathVariable("Memphoto") String Memphoto) throws IOException {
		Path fileUploadPath = Paths.get(memberFolder).resolve(Memphoto).normalize();

		Resource resc = new UrlResource(fileUploadPath.toUri());

		if (!resc.exists()) {
			return ResponseEntity.notFound().build();
		}
		String contentT = Files.probeContentType(fileUploadPath);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentT)).body(resc);
	}

	@PostMapping("/member.info.edit")
	public ResponseEntity<?> MeminfoEdit(
			@RequestParam(name = "ephoto", required = false) MultipartFile eph,
			@RequestParam("ecurpw") String curpw,
			@RequestParam(name="epw", required = false) String pw,
			@RequestParam(name="enick", required = false) String nck,
			@RequestParam("eid") String eid, HttpServletRequest req) {
		Map<String, Object> updateRetunerMap = new HashMap<>();
		Member loginMember = (Member) req.getSession().getAttribute("loginMember");
		LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");

		if (bcpe.matches(curpw, loginToken.getLoginPw()) && loginToken.getLoginId().equals(eid)) {
			System.out.println("패스워드&아이디 확인됨");
			updateRetunerMap = mDAO.MemberinfoUpdate(eph, pw, nck, req, loginMember, updateRetunerMap);
		}
		return ResponseEntity.ok().body(Map.of("resultD", updateRetunerMap));
	}

	@PostMapping("/member.exit")
	public ResponseEntity<?> memberExit(HttpServletRequest req, @RequestBody RegIdCheck RIC) {
		Member MDTO = (Member) req.getSession().getAttribute("loginMember");
		LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");
		Map<String, Object> resultretuner = new HashMap<>();

		String result = "탈퇴실패";
		System.out.println(result);
		String userinputpw = RIC.getRegid();
		if (bcpe.matches(userinputpw, loginToken.getLoginPw())) {
			result = mDAO.MemberExitLoc(loginToken, result);
			// 삭제처리
			new File(memberFolder + "/" + MDTO.getMmempphoto()).delete();
		}
		resultretuner.put("result", result);
		return ResponseEntity.ok().body(Map.of("resultD", resultretuner));
	}

}