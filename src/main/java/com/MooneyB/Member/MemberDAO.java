package com.MooneyB.Member;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.MooneyB.LeeFileNameGenerator;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class MemberDAO {
	private BCryptPasswordEncoder bcpe;
	private SimpleDateFormat sdf;
	
	@Autowired
	private MemberRepo mr;
	
	@Value("${mem.member.image}")
	private String memberFolder;

	@Value("${member.photo.size}")
	private int photoSize;
	
	public void getSaveMemberData(@RequestBody MemberDTO MDTO, MultipartFile mf,  HttpServletRequest req) {
		
		String fileName = null;
		
		try {
			if (mf.getSize() > photoSize) {
				throw new Exception();
			}
			fileName = LeeFileNameGenerator.generateFileName(mf);
			mf.transferTo(new File(memberFolder + "/" + fileName));
			MDTO.setPphoto(fileName);
			MDTO.setPw(bcpe.encode(MDTO.getPw()));
			MDTO.setBir(MDTO.getBir());

			if (mr.existsById(MDTO.getId())) {
				throw new Exception();
			}
			
			mr.save(MDTO);
			
		} catch (Exception e) {
			
			new File(memberFolder + "/" + fileName).delete();
			
			e.printStackTrace();
		}
	}
}
