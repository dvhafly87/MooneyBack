package com.MooneyB;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class LeeFileNameGenerator {
	public static String generateFileName(MultipartFile mf) {
		try {
			String fileName = mf.getOriginalFilename() + "";
			String type = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.replace(type, "");
			String uuid = UUID.randomUUID().toString();			
			return fileName + "_" + uuid + type;			
		} catch (Exception e) {
			return null;
		}
	}
}
