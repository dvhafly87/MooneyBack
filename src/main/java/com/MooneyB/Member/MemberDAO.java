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
public class MemberDAO {	
	
	@Autowired
	private BCryptPasswordEncoder bcpe;
	
	@Autowired
	private MemberRepo mr;
	
	@Value("${mem.member.image}")
	private String memberFolder;

	@Value("${member.photo.size}")
	private int photoSize;
	
	public void getSaveMemberData(MemberDTO MDTO, MultipartFile mf,  HttpServletRequest req, String[] memInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String fileName = null;
		
		try {
			
			if(mf != null) { 
				if (mf.getSize() > photoSize) {
					throw new Exception();
					
				}
				fileName = LeeFileNameGenerator.generateFileName(mf);
				mf.transferTo(new File(memberFolder + "/" + fileName));
				MDTO.setMmempphoto(fileName);				
			} else {
				MDTO.setMmempphoto(null);
			}
			
			//비번
			MDTO.setMmempw(bcpe.encode(memInfo[1]));
			
			//문자열 처리 
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
			new File(memberFolder + "/" + fileName).delete();
			e.printStackTrace();
		}
	}
	
	public void login(@RequestBody LoginDTO lDTO, HttpServletRequest req, Map<String, Boolean> result) {
		try {
	        Optional<MemberDTO> memberTemp = mr.findById(lDTO.getLoginId());
	        if (memberTemp.isPresent()) {
	            MemberDTO dbMember = memberTemp.get();
	            if (bcpe.matches(lDTO.getLoginPw(), dbMember.getMmempw())) {
	                req.getSession().setAttribute("loginMember", dbMember);
	                req.getSession().setMaxInactiveInterval(1000000);
	                result.put("loginok", true);
	            } else {
	                result.put("loginok", false);
	            }
	        } else {
	            result.put("loginok", false);
	        }
	    } catch (Exception e) {
	        result.put("loginok", false);
	        e.printStackTrace();
	    }
	}
	
	public boolean isLogined(HttpServletRequest req) {
		MemberDTO m = (MemberDTO) req.getSession().getAttribute("loginMember");
		if (m != null) {
			req.setAttribute("loginPage", "Member/login");
			req.getSession().getAttribute("loginMember");
			return true;
		}
		req.setAttribute("loginPage", "Member/unlogin");
		req.getSession().setAttribute("loginMember", null);
		return false;
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
}
