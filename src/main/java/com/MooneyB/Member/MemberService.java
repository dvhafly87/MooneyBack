package com.MooneyB.Member;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.MooneyB.LeeFileNameGenerator;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class MemberService {


	@Autowired
	private BCryptPasswordEncoder bcpe;

	@Autowired
	private MemberRepo mr;

	@Value("${mem.member.image}")
	private String memberFolder;

	@Value("${member.photo.size}")
	private int photoSize;

	public void getSaveMemberData(Member MDTO, MultipartFile mf, HttpServletRequest req, String[] memInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = null;

		try {
			if (mf != null) {
				if (mf.getSize() > photoSize) {
					throw new Exception();
				}
				fileName = LeeFileNameGenerator.generateFileName(mf);
				mf.transferTo(new File(memberFolder + "/" + fileName));
				MDTO.setMmempphoto(fileName);
			} else {
				MDTO.setMmempphoto(null);
			}

			// 비번
			MDTO.setMmempw(bcpe.encode(memInfo[1]));

			// 문자열 처리
			Date birth = sdf.parse(memInfo[3]);
			MDTO.setMmembir(birth);

			MDTO.setMmemid(memInfo[0]);
			MDTO.setMmemnick(memInfo[2]);

			if (mr.existsById(memInfo[0])) {
				throw new Exception();
			}

			Date now = new Date();
			String todayStr = sdf.format(now);
			Date today = sdf.parse(todayStr);

			MDTO.setMmemregd(today);
			MDTO.setMmemppnt(0);
			mr.save(MDTO);

		} catch (Exception e) {
			// 실패시 업로드 이미지 삭제 처리
			new File(memberFolder + "/" + fileName).delete();
			e.printStackTrace();
		}
	}

	public boolean login(@RequestBody LoginDTO lDTO, HttpServletRequest req, Map<String, Object> result) {
		try {
			Optional<Member> memberTemp = mr.findById(lDTO.getLoginId());

			if (memberTemp.isPresent()) {
				System.out.println("id correct");
				Member dbMember = memberTemp.get();

				if (bcpe.matches(lDTO.getLoginPw(), dbMember.getMmempw())) {
					System.out.println("매치성공?");

					// 로그인 성공 시 세션에 정보 저장
					LoginDTO loginToken = new LoginDTO();
					loginToken.setLoginId(dbMember.getMmemid());
					loginToken.setLoginPw(dbMember.getMmempw());

					req.getSession().setAttribute("loginMember", dbMember);
					req.getSession().setAttribute("LoginToken", loginToken);

					result.put("loginok", true);
					return true;
				} else {
					System.out.println("비번 틀");
					result.put("loginok", false);
					return false;
				}
			} else {
			System.out.println("아이디 틀");
				result.put("loginok", false);
				return false;
			}
		} catch (Exception e) {
			result.put("loginok", false);
			e.printStackTrace();
			return false;
		}
	}

	public boolean isLogined(HttpServletRequest req) {
		Member loginMember = (Member) req.getSession().getAttribute("loginMember");
		LoginDTO loginToken = (LoginDTO) req.getSession().getAttribute("LoginToken");

		return loginMember != null && loginToken != null;
	}

	public String RegisterIDCheck(String id, String retuna) {
		if (mr.findByMmemid(id) == null) {
			retuna = "ok";
			return retuna;
		} else {
			retuna = "nop";
			return retuna;
		}
	}

	public String RegisterNCCheck(String nc, String retuna) {
		if (mr.findByMmemnick(nc) == null) {
			retuna = "ok";
			return retuna;
		} else {
			retuna = "nop";
			return retuna;
		}
	}

	public void MeminfoSelect(String idinfo, Map<String, Object> memDTA, HttpServletRequest req) {
		if (mr.findAllById(idinfo) != null) {
			Member m = (Member) req.getSession().getAttribute("loginMember");
			if (m != null) {
				System.out.println(m.getMmemppnt());
			}
		}
	}

	public String MemberExitLoc(LoginDTO loginToken, String result) {
		mr.deleteById(loginToken.getLoginId());
		result = "탈퇴 완료";
		return result;
	}

	public Map<String, Object> MemberinfoUpdate(MultipartFile eph, String pw, String nck, HttpServletRequest req,
			Member loginMember, Map<String, Object> updateRetunerMap) {
		String fileName = null;
		try {
			if (eph != null) {

				if (loginMember.getMmempphoto() != null) {
					new File(memberFolder + "/" + loginMember.getMmempphoto()).delete();
				}

				if (eph.getSize() > photoSize) {
					throw new Exception();
				}

				fileName = LeeFileNameGenerator.generateFileName(eph);
				eph.transferTo(new File(memberFolder + "/" + fileName));

				// 이미지 처리?
				loginMember.setMmempphoto(fileName);

			} else {
				System.out.println("이미지 수정 요청 없음");
			}
			System.out.println(nck + pw);


			if (pw != null && nck != null) {// 비밀번호 재인코딩
				loginMember.setMmemnick(nck);
				loginMember.setMmempw(bcpe.encode(pw));
				
			}
			
			loginMember.setMmemnick(loginMember.getMmemnick());
			loginMember.setMmempw(loginMember.getMmempw());
			
			mr.save(loginMember);
			updateRetunerMap.put("result", true);
		} catch (Exception e) {
			updateRetunerMap.put("result", false);
		}
		return updateRetunerMap;
	}
}