package com.MooneyB.llm3;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class Llama3Service {

	private final WebClient webClient;
	
	public Llama3Service(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://dvhafly87.kmgproj.p-e.kr:3339").build();
	}
	
	public String generateFromLlama3(String userMessage) {
        Map<String, Object> requestBody = Map.of(
            "model", "llama3",
            "prompt", userMessage,
            "temperature", 0.7,
            "top_p", 0.9,
            "max_tokens", 200,
            "stop", new String[] {"</s>"}
        );

        return webClient.post()
            .uri("/api/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block(); 
    }
	public void DataAnalyzing(String userinput, String useinfo) {
		
	}
}
