package com.MooneyB;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

public class DateManager {
	public static void getCurYear(HttpServletRequest request) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String curYear = sdf.format(now);
		int curYear2 = Integer.parseInt(curYear);
		request.setAttribute("curYear", curYear2);
	}
}
