 package com.MooneyB.llm3;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class Llm3Controller {
	
	@Autowired
	private Llama3Service lm3;
	
	@PostMapping("/llama3-api")
	public ResponseEntity<?> GeneratingFirstMessage(
			HttpServletRequest req,
			@RequestParam("userInput") String userinput,
			@RequestParam("userinfo") String useinfo
			) {
		
		try {
			System.out.println(userinput);
			System.out.println(useinfo);
			String DataReturn = lm3.DataAnalyzing(userinput, useinfo);
			System.out.println(DataReturn);
			String result = lm3.generateFromLlama3(DataReturn);
			String decodlt = decodeLlama3Response(result);
			System.out.println(decodlt);
			String trs = lm3.Translationto(decodlt);
			System.out.println(trs);
			
			return ResponseEntity.ok().body(Map.of("translation", trs));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}
	}
	
	public static String decodeLlama3Response(String responseStr) throws Exception {
	    StringBuilder sb = new StringBuilder();
	    ObjectMapper om = new ObjectMapper();
	    String[] lines = responseStr.split("\\R");
	    
	    for (String line : lines) {
	        if (line.strip().isEmpty()) continue;

	        JsonNode node = om.readTree(line);
	        if (node.has("response")) {
	            sb.append(node.get("response").asText());
	        }
	    }
	    return sb.toString();
	}

}
