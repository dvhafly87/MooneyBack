package com.MooneyB.llm3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class Llm3Controller {

	private final Llama3Service lm3;

	public Llm3Controller(Llama3Service llama3Service) {
		this.lm3 = llama3Service;
	}
	
	@PostMapping("/llama3-api")
	public ResponseEntity<?> GeneratingFirstMessage(
			HttpServletRequest req,
			@RequestParam("userInput") String userinput,
			@RequestParam("userinfo") String useinfo
			) {
		System.out.println(userinput + useinfo);
		lm3.DataAnalyzing(userinput, useinfo);
//		String result = lm3.generateFromLlama3(userinput);
		return ResponseEntity.ok(null);
	}
}
