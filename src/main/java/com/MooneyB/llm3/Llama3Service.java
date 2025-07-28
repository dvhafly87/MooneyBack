package com.MooneyB.llm3;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Collections.reverseOrder;

@Service
public class Llama3Service {

	@Value("${google.cloud.api.key}")
	private String gaik;

	@Autowired
	private Llama3Repository lr;

	private final WebClient webClient;

	private final Map<String, BiFunction<String, String, String>> AnalUserinput = new HashMap<>();

	public Llama3Service(WebClient.Builder webClientBuilder) {
		// AI 모델 api 서버 프록시 연결 파츠
		this.webClient = webClientBuilder.baseUrl("https://dvhafly87.kmgproj.p-e.kr:3339").build();

		// 태스트를 위한 일부 같은 메서드 지정
		AnalUserinput.put("이번주 지출 분석", this::WeeklyDataAnal);
		AnalUserinput.put("이번달 소비 패턴 분석", this::MonthlyDataAnal);
		AnalUserinput.put("고정 지출 패턴 분석", this::getAnalRPTData);
		AnalUserinput.put("지난달 대비 이번달 소비 추이 분석", this::getMonthlyTrendAnalysisData);
		AnalUserinput.put("주요 소비처 분석", this::getAmtAndDecData);
		AnalUserinput.put("지난달 대비 이번달 지출 빈도 추이 분석", this::getCNTInfoCurrentAndLatelyMonth);
		AnalUserinput.put("이번달 고액 지출건 분석", this::getHighPriceData);
	}

	public String generateFromLlama3(String userMessage) {
		String AIRequest = "You are Mooney, an expert AI assistant dedicated to analyzing and explaining personal spending data. "
				+ "Your responses must be highly structured, actionable, and easy to read, using proper HTML formatting ONLY as specified below. "
				+ "<br><br>" + "<b>Formatting & Presentation Rules (MANDATORY):</b><br>"
				+ "- Use only HTML tags for all styling.<br>"
				+ "- All line breaks must be rendered with <br> (single line), all paragraph breaks must be <br><br> (double line). Never use \\n or Markdown for newlines.<br>"
				+ "- Use <b> and </b> for all bold text (section titles, highlights).<br>"
				+ "- Use <ul><li> or <ol><li> for all lists; do not use dashes, asterisks, or Markdown.<br>"
				+ "- All numbers, dates, and currencies must include clear units (e.g., KRW, $/USD, %, days).<br>"
				+ "- Make sure results display perfectly in both desktop and mobile chat interfaces.<br><br>" +

				"<b>Request Context:</b><br>" + "The user has asked for: {" + userMessage + "}.<br>"
				+ "Base your analysis strictly on the data and type of request provided.<br><br>" +

				"<b>Analysis Instructions:</b><br>" + "<ol>"
				+ "<li>Start with a concise, personalized summary of the key findings, bolding all section headings.</li>"
				+ "<li>Clearly explain the results for each requested area (e.g., daily summary, monthly trend, categories, merchant analysis, fixed expenses, or others).</li>"
				+ "<li>Highlight data-backed insights, patterns, anomalies, or behavioral trends. Use <b>percentages</b>, <b>comparisons</b>, and <b>clear breakdowns by category or time period</b>.</li>"
				+ "<li>Offer highly practical and creative recommendations, linking advice directly to the user’s results.</li>"
				+ "<li>Compare with previous periods or peer averages if possible, and highlight causes of unusual changes or spending spikes.</li>"
				+ "<li>Always write in a friendly, reassuring, intelligent tone tailored for Korean users; use simple language with clear, short sentences.</li>"
				+ "<li>Never include any Markdown. Never use \\n for line breaks under any circumstances.</li>"
				+ "<li>Format all lists with <ul><li> or <ol><li>, never with asterisks or dashes.</li>"
				+ "<li>End your response with a motivational or positive closing remark, referencing ‘wise financial decisions’.</li>"
				+ "</ol>"
				+ "Strictly adhere to the following format when responding — no exceptions: <br><strong>Topic</strong><br>- Detailed content<br/>."
				+ "You are Mooney, the AI chatbot of our project — act and respond strictly in accordance with Mooney’s unique character, which is calm and gentle like a cow. Do not break character under any circumstances."
				+ "You must end every paragraph with a <br/> tag to clearly separate each section — this is mandatory."
				+ "For maximum clarity, you are required to use HTML tags generously throughout your responses — especially <br/> for line breaks and <strong> for emphasis. Readability is a top priority, and improper formatting will not be accepted."
				+ "<br><b>Current Date/Time:</b> " + "Wednesday, July 16, 2025, 3:19 PM KST" + "<br>";

		Map<String, Object> requestBody = Map.of("model", "llama3", "prompt", AIRequest, "temperature", 0.7, "top_p",
				0.9, "max_tokens", 200, "stop", new String[] { "</s>" }

		);

		return webClient.post().uri("/api/generate").contentType(MediaType.APPLICATION_JSON).bodyValue(requestBody)
				.retrieve().bodyToMono(String.class).block();
	}

	public String Translationto(String result) {
			RestTemplate rest = new RestTemplate();
			String response = null;
			String body = "";
			HttpEntity<String> requestEntity = new HttpEntity<String>(body);
			ResponseEntity<String> responseEntity = rest.exchange(
				    "https://translation.googleapis.com/language/translate/v2?q=" + result
				    + "&target=ko&source=en&format=html&key=" + gaik,
				    HttpMethod.GET, requestEntity, String.class);
			response = responseEntity.getBody();
			System.out.println(response);
			String translatedText = null;

			JSONObject jsonResponse = new JSONObject(response);
			
			//기존 json 형태의 데이터를 재구성하여 번역 텍스트만 추출
			if (jsonResponse.has("data")) {
				JSONObject data = jsonResponse.getJSONObject("data");
				if (data.has("translations")) {
					translatedText = data.getJSONArray("translations").getJSONObject(0).getString("translatedText");
				}
			}
			
			translatedText = translatedText.replace("&quot;", "|")
					.replace("&#39;", "'").replace("&amp;", "&")
					.replace("&lt;", "<").replace("&gt;", ">")
					.replace("*", " ").replace("&nbsp;", " ")
					.replace("<b>", "<br>").replace("</b>", "</br>")
					.replace("\"", "").replace(":", "")
					.replace("KRW", "원").replace("\n", "<br/>");
			
			return translatedText;
	}

	public String DataAnalyzing(String userinput, String useinfo) {
		BiFunction<String, String, String> function = AnalUserinput.get(userinput);

		if (function != null) {
			return function.apply(userinput, useinfo);
		} else {
			return null;
		}
	}

	// 사용자의 금주 지출 가져와서 프롬프트로 가
	public String WeeklyDataAnal(String userinput, String useinfo) {
		List<String> WeeklyData = lr.getWeeklyExpenseSummary(useinfo);
		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		if (WeeklyData != null && !WeeklyData.isEmpty()) {
			res.append("🗂️ Expense Data Summary (Grouped by Week):\n");
			String currentWeek = "";
			int totalAmount = 0;
			Map<String, Integer> categoryTotals = new HashMap<>();
			Map<String, Integer> dailyTotals = new HashMap<>();

			for (int i = 0; i < WeeklyData.size(); i++) {
				String listCount = WeeklyData.get(i);
				String[] splitor = listCount.split(",");

				// 주간 기간 구분
				if (!splitor[0].equals(currentWeek)) {
					currentWeek = splitor[0];
					res.append("\n📅 [Weekly Period: ").append(currentWeek).append("]\n");
				}

				String date = splitor[1].split(" ")[0]; // 날짜 추출
				String category = splitor[2];
				int amount = Integer.parseInt(splitor[3]);
				String formattedAmount = String.format("%,d", amount);

				// 통계 계산용 데이터 수집
				totalAmount += amount;
				categoryTotals.put(category, categoryTotals.getOrDefault(category, 0) + amount);
				dailyTotals.put(date, dailyTotals.getOrDefault(date, 0) + amount);

				res.append(String.format("%2d. Date: %s | Category: %s | Amount: %s KRW\n", i + 1, date, category,
						formattedAmount));
			}

			// 계산된 통계 정보 추가
			res.append("\n📊 CALCULATED STATISTICS (DO NOT MODIFY):\n");
			res.append("- Total Entries: ").append(WeeklyData.size()).append("\n");
			res.append("- Total Amount: ").append(String.format("%,d", totalAmount)).append(" KRW\n");
			res.append("- Number of Days: ").append(dailyTotals.size()).append("\n");
			res.append("- Average per Day: ")
					.append(String.format("%,d", totalAmount / Math.max(1, dailyTotals.size()))).append(" KRW\n");

			res.append("\n📈 Category Breakdown:\n");
			for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
				res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue()))
						.append(" KRW\n");
			}

			res.append("\n📅 Daily Totals:\n");
			for (Map.Entry<String, Integer> entry : dailyTotals.entrySet()) {
				res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue()))
						.append(" KRW\n");
			}

			// 강화된 분석 지침
			res.append("\n🚨 CRITICAL ANALYSIS RULES:\n");
			res.append("1. NEVER create, assume, or add data not explicitly shown above\n");
			res.append("2. NEVER modify any amounts, dates, or categories\n");
			res.append("3. NEVER perform calculations different from the provided statistics\n");
			res.append("4. NEVER compare with data from other weeks unless explicitly provided\n");
			res.append("5. NEVER mention specific amounts not listed in the data\n");
			res.append("6. NEVER assume transaction details beyond what's shown\n");
			res.append("7. If you cannot analyze something due to insufficient data, explicitly state this\n");
			res.append("8. Use ONLY the calculated statistics provided above\n");
			res.append("9. Do NOT recalculate totals or averages - use the provided values\n");
			res.append("10. If asked about trends, state that comparison data is not available\n");

			// 분석 요청 항목 (더 구체적으로)
			res.append("\n📝 Analysis Requirements:\n");
			res.append("Based ONLY on the data and statistics above, provide:\n");
			res.append("- Summary of total spending (use provided total: ").append(String.format("%,d", totalAmount))
					.append(" KRW)\n");
			res.append("- Category analysis (use provided category breakdown)\n");
			res.append("- Daily spending patterns (use provided daily totals)\n");
			res.append("- General observations about spending behavior\n");
			res.append("- Practical suggestions for expense management\n");

			res.append("\n❌ DO NOT:\n");
			res.append("- Add fictional transactions or amounts\n");
			res.append("- Perform manual calculations (all calculations are provided)\n");
			res.append("- Reference data from other time periods\n");
			res.append("- Make assumptions about spending causes without evidence\n");
			res.append("- Suggest specific amounts to save without data basis\n");

			res.append("\n✅ WHEN UNCERTAIN:\n");
			res.append("- State: 'Based on the provided data...'\n");
			res.append("- State: 'The data shows...'\n");
			res.append("- State: 'Cannot determine without additional data'\n");
			res.append("- Ask for clarification if needed\n");

		} else {
			// 데이터가 없을 경우
			res.append("⚠️ No expense data was found for the requested period.\n");
			res.append("There may have been no transactions, or the data has not yet been recorded.\n\n");
			res.append("💡 Next Steps:\n");
			res.append("- Try selecting a different week or category.\n");
			res.append("- Ensure expenses are correctly logged in the system.\n");
			res.append("- You can request help with tracking or input formatting.\n");
			res.append("- Contact support if you believe this is an error.\n");
			res.append(
					"Since there is no data currently retrieved, please respond with a message indicating that no data is available.");
		}

		return res.toString();
	}

	public String MonthlyDataAnal(String userinput, String userinfo) {
	    List<String> MonthlyData = lr.getMonthlyExpenseSummary(userinfo); // User's expense data
	    List<String> getimpDTA = lr.getInspensiveData(userinfo); // User's income data JPA  
	    StringBuilder res = new StringBuilder();
	    
	    // Common header
	    res.append("====================================\n");
	    res.append("💬 User Request: ").append(userinput).append("\n");
	    res.append("====================================\n\n");
	    
	    // Initialize statistics variables
	    int totalExpenses = 0;
	    int totalIncome = 0;
	    Map<String, Integer> expenseCategoryTotals = new HashMap<>();
	    Map<String, Integer> dailyExpenseTotals = new HashMap<>();
	    Map<String, Integer> incomeTypeTotals = new HashMap<>();
	    
	    // Check if we have income data
	    boolean hasIncomeData = (getimpDTA != null && !getimpDTA.isEmpty());
	    // Check if we have expense data
	    boolean hasExpenseData = (MonthlyData != null && !MonthlyData.isEmpty());
	    
	    if (hasIncomeData || hasExpenseData) {
	        
	        // Process Income Data if available
	        if (hasIncomeData) {
	            res.append("💰 Income Data Summary:\n");
	            int incomeEntryCount = 0;
	            
	            for (String incomeData : getimpDTA) {
	                String[] incomeInfo = incomeData.split(",");
	                if (incomeInfo.length >= 4) {
	                    incomeEntryCount++;
	                    int salaryAmount = Integer.parseInt(incomeInfo[0].trim());
	                    String incomeType = incomeInfo[1].trim();
	                    String paymentDate = incomeInfo[2].trim();
	                    String nextPaymentDate = incomeInfo[3].trim();
	                    String formattedSalary = String.format("%,d", salaryAmount);
	                    
	                    // Statistics calculation
	                    totalIncome += salaryAmount;
	                    incomeTypeTotals.put(incomeType, incomeTypeTotals.getOrDefault(incomeType, 0) + salaryAmount);
	                    
	                    res.append(String.format("%2d. Salary: %s KRW | Type: %s | Payment Date: %s | Next Payment: %s\n", 
	                        incomeEntryCount, formattedSalary, incomeType, paymentDate, nextPaymentDate));
	                }
	            }
	            
	            res.append("\n📊 INCOME STATISTICS:\n");
	            res.append("- Total Income Entries: ").append(getimpDTA.size()).append("\n");
	            res.append("- Total Income Amount: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
	            
	            res.append("\n💼 Income Type Breakdown:\n");
	            for (Map.Entry<String, Integer> entry : incomeTypeTotals.entrySet()) {
	                res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue())).append(" KRW\n");
	            }
	        }
	        
	        // Process Expense Data if available
	        if (hasExpenseData) {
	            res.append("\n🗂️ Expense Data Summary (Grouped by Month):\n");
	            String currentMonth = "";
	            
	            for (int i = 0; i < MonthlyData.size(); i++) {
	                String expenseData = MonthlyData.get(i);
	                String[] expenseInfo = expenseData.split(",");
	                
	                if (expenseInfo.length >= 4) {
	                    // Monthly period distinction
	                    if (!expenseInfo[0].equals(currentMonth)) {
	                        currentMonth = expenseInfo[0];
	                        res.append("\n📅 [Monthly Period: ").append(currentMonth).append("]\n");
	                    }
	                    
	                    String date = expenseInfo[1].split(" ")[0]; // Extract date
	                    String category = expenseInfo[2].trim();
	                    int amount = Integer.parseInt(expenseInfo[3].trim());
	                    String formattedAmount = String.format("%,d", amount);
	                    
	                    // Statistics calculation
	                    totalExpenses += amount;
	                    expenseCategoryTotals.put(category, expenseCategoryTotals.getOrDefault(category, 0) + amount);
	                    dailyExpenseTotals.put(date, dailyExpenseTotals.getOrDefault(date, 0) + amount);
	                    
	                    res.append(String.format("%2d. Date: %s | Category: %s | Amount: %s KRW\n", 
	                        i + 1, date, category, formattedAmount));
	                }
	            }
	            
	            res.append("\n📊 EXPENSE STATISTICS:\n");
	            res.append("- Total Expense Entries: ").append(MonthlyData.size()).append("\n");
	            res.append("- Total Expense Amount: ").append(String.format("%,d", totalExpenses)).append(" KRW\n");
	            res.append("- Number of Spending Days: ").append(dailyExpenseTotals.size()).append("\n");
	            res.append("- Average per Spending Day: ")
	                    .append(String.format("%,d", totalExpenses / Math.max(1, dailyExpenseTotals.size()))).append(" KRW\n");
	            
	            res.append("\n📈 Expense Category Breakdown:\n");
	            for (Map.Entry<String, Integer> entry : expenseCategoryTotals.entrySet()) {
	                res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue())).append(" KRW\n");
	            }
	            
	            res.append("\n📅 Daily Expense Totals:\n");
	            for (Map.Entry<String, Integer> entry : dailyExpenseTotals.entrySet()) {
	                res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue())).append(" KRW\n");
	            }
	        }
	        
	        // Financial Balance Summary (if both income and expense data available)
	        if (hasIncomeData && hasExpenseData) {
	            int netBalance = totalIncome - totalExpenses;
	            res.append("\n💹 FINANCIAL BALANCE SUMMARY:\n");
	            res.append("- Total Income: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
	            res.append("- Total Expenses: ").append(String.format("%,d", totalExpenses)).append(" KRW\n");
	            res.append("- Net Balance: ").append(String.format("%,d", netBalance)).append(" KRW ");
	            res.append(netBalance >= 0 ? "(SURPLUS ✅)" : "(DEFICIT ⚠️)").append("\n");
	            
	            if (totalIncome > 0) {
	                double savingsRate = ((double) netBalance / totalIncome) * 100;
	                res.append("- Savings Rate: ").append(String.format("%.1f", savingsRate)).append("%\n");
	            }
	        }
	        
	        // Enhanced analysis guidelines
	        res.append("\n🚨 CRITICAL ANALYSIS RULES:\n");
	        res.append("1. NEVER create, assume, or add data not explicitly shown above\n");
	        res.append("2. NEVER modify any amounts, dates, categories, or income types\n");
	        res.append("3. NEVER perform calculations different from the provided statistics\n");
	        res.append("4. NEVER compare with data from other months unless explicitly provided\n");
	        res.append("5. NEVER mention specific amounts not listed in the data\n");
	        res.append("6. NEVER assume transaction or income details beyond what's shown\n");
	        res.append("7. If you cannot analyze something due to insufficient data, explicitly state this\n");
	        res.append("8. Use ONLY the calculated statistics provided above\n");
	        res.append("9. Do NOT recalculate totals, averages, or balances - use the provided values\n");
	        res.append("10. If asked about trends, state that comparison data is not available\n");
	        
	        // Enhanced Weekly Analysis (for expense data)
	        if (hasExpenseData) {
	            res.append("\n📈 WEEKLY EXPENSE ANALYSIS:\n");
	            Map<String, Integer> weeklyTotals = new HashMap<>();
	            
	            for (String expenseData : MonthlyData) {
	                String[] expenseInfo = expenseData.split(",");
	                if (expenseInfo.length >= 4) {
	                    String date = expenseInfo[1].split(" ")[0];
	                    int amount = Integer.parseInt(expenseInfo[3].trim());
	                    
	                    // Calculate week number from date (simplified)
	                    String weekKey = "Week " + ((Integer.parseInt(date.split("-")[2]) - 1) / 7 + 1);
	                    weeklyTotals.put(weekKey, weeklyTotals.getOrDefault(weekKey, 0) + amount);
	                }
	            }
	            
	            res.append("Weekly Spending Breakdown:\n");
	            String highestSpendingWeek = "";
	            int highestWeeklyAmount = 0;
	            
	            for (Map.Entry<String, Integer> entry : weeklyTotals.entrySet()) {
	                res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue())).append(" KRW\n");
	                if (entry.getValue() > highestWeeklyAmount) {
	                    highestWeeklyAmount = entry.getValue();
	                    highestSpendingWeek = entry.getKey();
	                }
	            }
	            
	            if (!highestSpendingWeek.isEmpty()) {
	                res.append("- Highest Spending Week: ").append(highestSpendingWeek)
	                   .append(" (").append(String.format("%,d", highestWeeklyAmount)).append(" KRW)\n");
	            }
	        }
	        
	        // Category Analysis Enhancement
	        if (hasExpenseData && !expenseCategoryTotals.isEmpty()) {
	            String topCategory = "";
	            int topCategoryAmount = 0;
	            for (Map.Entry<String, Integer> entry : expenseCategoryTotals.entrySet()) {
	                if (entry.getValue() > topCategoryAmount) {
	                    topCategoryAmount = entry.getValue();
	                    topCategory = entry.getKey();
	                }
	            }
	            
	            res.append("\n🏆 TOP SPENDING INSIGHTS:\n");
	            res.append("- Highest Spending Category: ").append(topCategory)
	               .append(" (").append(String.format("%,d", topCategoryAmount)).append(" KRW)\n");
	            
	            if (totalExpenses > 0) {
	                double categoryPercentage = ((double) topCategoryAmount / totalExpenses) * 100;
	                res.append("- This category represents ").append(String.format("%.1f", categoryPercentage))
	                   .append("% of total monthly expenses\n");
	            }
	        }
	        
	        // Analysis requirements
	        res.append("\n📝 DETAILED ANALYSIS REQUIREMENTS:\n");
	        res.append("Based ONLY on the data and statistics above, provide comprehensive analysis including:\n");
	        
	        if (hasIncomeData && hasExpenseData) {
	            double expenseRatio = ((double) totalExpenses / totalIncome) * 100;
	            res.append("\n🎯 MANDATORY ANALYSIS POINTS:\n");
	            res.append("1. 💰 EXPENSE-TO-INCOME RATIO:\n");
	            res.append("   - Total expenses represent ").append(String.format("%.1f", expenseRatio))
	               .append("% of total income (").append(String.format("%,d", totalExpenses))
	               .append(" KRW out of ").append(String.format("%,d", totalIncome)).append(" KRW)\n");
	            res.append("   - Analyze if this ratio is healthy/concerning and provide specific recommendations\n");
	            
	            res.append("\n2. 🔍 SPENDING PATTERN ANALYSIS:\n");
	            res.append("   - Examine the expense data patterns and identify potential reasons for spending\n");
	            res.append("   - Look for patterns in dates, categories, and amounts\n");
	            res.append("   - ONLY make observations based on visible data patterns\n");
	            
	            res.append("\n3. 📅 WEEKLY SPENDING COMPARISON:\n");
	            res.append("   - Identify which week had the highest spending using provided weekly breakdown\n");
	            res.append("   - Compare weekly spending patterns if multiple weeks of data exist\n");
	            
	            res.append("\n4. 📊 CATEGORY DOMINANCE ANALYSIS:\n");
	            res.append("   - Identify the highest spending category using provided category breakdown\n");
	            res.append("   - Analyze what percentage this category represents of total spending\n");
	            res.append("   - Suggest whether this level of spending in this category is appropriate\n");
	            
	            res.append("\n5. 💡 FINANCIAL HEALTH & ACTIONABLE RECOMMENDATIONS:\n");
	            res.append("   - Assess overall financial health based on income vs expenses\n");
	            res.append("   - Provide specific, actionable budgeting recommendations\n");
	            res.append("   - Suggest realistic expense reduction strategies for top spending categories\n");
	            res.append("   - Recommend optimal expense-to-income ratios for different categories\n");
	            res.append("   - Identify potential emergency fund building opportunities\n");
	            
	            res.append("\n6. 🚨 ADDITIONAL INSIGHTS & ALERTS:\n");
	            res.append("   - Identify any concerning spending spikes or unusual patterns\n");
	            res.append("   - Highlight days with exceptionally high spending\n");
	            res.append("   - Assess spending consistency vs irregular large purchases\n");
	            res.append("   - Compare spending frequency across different categories\n");
	            res.append("   - Identify potential areas for subscription/recurring cost optimization\n");
	            
	            res.append("\n7. 📈 TREND ANALYSIS & PROJECTIONS:\n");
	            res.append("   - Analyze if current spending rate is sustainable\n");
	            res.append("   - Project monthly savings potential based on current patterns\n");
	            res.append("   - Identify which expense categories have room for optimization\n");
	            res.append("   - Suggest realistic monthly budgets for each category\n");
	            
	            res.append("\n8. 🎯 PERSONALIZED ACTION PLAN:\n");
	            res.append("   - Create 3 specific, measurable financial goals for next month\n");
	            res.append("   - Prioritize which spending areas need immediate attention\n");
	            res.append("   - Suggest tracking methods for better financial awareness\n");
	            res.append("   - Recommend tools or habits to maintain financial discipline\n");
	            
	        } else if (hasExpenseData) {
	            res.append("\n🎯 EXPENSE-ONLY ANALYSIS POINTS:\n");
	            res.append("1. 📊 SPENDING BREAKDOWN:\n");
	            res.append("   - Total expenses: ").append(String.format("%,d", totalExpenses)).append(" KRW\n");
	            res.append("   - Analyze spending patterns and frequency\n");
	            
	            res.append("\n2. 🔍 CATEGORY & WEEKLY ANALYSIS:\n");
	            res.append("   - Identify highest spending category and week\n");
	            res.append("   - Analyze spending distribution and patterns\n");
	            
	            res.append("\n3. ⚠️ LIMITATIONS & RECOMMENDATIONS:\n");
	            res.append("   - Note that income data is unavailable for complete analysis\n");
	            res.append("   - Provide general expense management suggestions\n");
	            res.append("   - Recommend income tracking for better financial planning\n");
	            
	        } else if (hasIncomeData) {
	            res.append("\n🎯 INCOME-ONLY ANALYSIS POINTS:\n");
	            res.append("1. 💰 INCOME ASSESSMENT:\n");
	            res.append("   - Total income: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
	            res.append("   - Analyze income sources and stability\n");
	            
	            res.append("\n2. 📋 BUDGETING FRAMEWORK:\n");
	            res.append("   - Suggest appropriate expense categories and limits\n");
	            res.append("   - Recommend budgeting ratios (50/30/20 rule, etc.)\n");
	            
	            res.append("\n3. ⚠️ LIMITATIONS:\n");
	            res.append("   - Note that expense data is unavailable\n");
	            res.append("   - Recommend expense tracking for complete financial analysis\n");
	        }
	        
	        res.append("\n❌ DO NOT:\n");
	        res.append("- Add fictional transactions, income, or amounts\n");
	        res.append("- Perform manual calculations (all calculations are provided)\n");
	        res.append("- Reference data from other time periods\n");
	        res.append("- Make assumptions about spending/income causes without evidence\n");
	        res.append("- Suggest specific amounts to save/spend without data basis\n");
	        res.append("- Create hypothetical scenarios or projections\n");
	        
	        res.append("\n✅ WHEN UNCERTAIN:\n");
	        res.append("- State: 'Based on the provided data...'\n");
	        res.append("- State: 'The data shows...'\n");
	        res.append("- State: 'Cannot determine without additional data'\n");
	        res.append("- Ask for clarification if needed\n");
	        res.append("- Acknowledge data limitations explicitly\n");
	        
	    } else {
	        // No data available case
	        res.append("⚠️ No financial data was found for the requested monthly period.\n");
	        res.append("Neither income nor expense data is currently available.\n\n");
	        res.append("💡 Next Steps:\n");
	        res.append("- Try selecting a different month or time period.\n");
	        res.append("- Ensure both income and expenses are correctly logged in the system.\n");
	        res.append("- You can request help with data entry or input formatting.\n");
	        res.append("- Contact support if you believe this is an error.\n");
	        res.append("- Consider setting up automatic data tracking for future months.\n");
	        res.append("Since there is no financial data currently retrieved, please respond with a message indicating that no data is available and suggest general financial management advice.");
	    }
	    
	    return res.toString();
	}
	
	
	// 반복 지출 데이터 가져와서 프롬프트 가공 (수입데이터 포함 개선)
	public String getAnalRPTData(String userinput, String userinfo) {
		List<String> getRPTDTA = lr.getRepeatPriceData(userinfo); // 반복지출 JPA
		List<String> getimpDTA = lr.getInspensiveData(userinfo); // 수입데이터 JPA

		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		// 수입 데이터 처리 (구독 데이터 전에 배치)
		if (getimpDTA != null && !getimpDTA.isEmpty()) {
			res.append("💰 User Income Information:\n");
			int totalIncome = 0;
			int incomeCount = 0;

			for (int j = 0; j < getimpDTA.size(); j++) {
				String getDTA = getimpDTA.get(j);
				String[] userimportDTA = getDTA.split(",");

				// 배열 길이 확인 후 처리
				if (userimportDTA.length >= 3) {
					String amount = userimportDTA[0];
					String incomeType = userimportDTA[1];
					String paymentDate = userimportDTA[2];

					int incomeAmount = Integer.parseInt(amount.replaceAll("[^0-9]", "")); // 숫자만 추출
					totalIncome += incomeAmount;
					incomeCount++;

					String formattedIncome = String.format("%,d", incomeAmount);

					res.append(String.format("%2d. Income Type: %s\n", j + 1, incomeType));
					res.append(String.format("    Amount: %s KRW\n", formattedIncome));
					res.append(String.format("    Payment Date: %s\n\n", paymentDate));
				}
			}

			// 수입 통계
			res.append("📊 Income Summary:\n");
			res.append("- Total Monthly Income: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
			res.append("- Number of Income Sources: ").append(incomeCount).append("\n");
			res.append("- Average per Source: ").append(String.format("%,d", totalIncome / Math.max(1, incomeCount)))
					.append(" KRW\n\n");
		} else {
			res.append("⚠️ No income data available.\n\n");
		}

		if (getRPTDTA != null && !getRPTDTA.isEmpty()) {
			res.append("🔄 Recurring Subscription Data Summary:\n");
			int totalMonthlyAmount = 0;
			int totalYearlyAmount = 0;
			int totalEntries = 0;
			Map<String, Integer> categoryTotals = new HashMap<>();
			Map<String, Integer> frequencyTotals = new HashMap<>();
			Map<String, String> subscriptionDetails = new HashMap<>();

			for (int i = 0; i < getRPTDTA.size(); i++) {
				String listCount = getRPTDTA.get(i);
				String[] splitor = listCount.split(",");

				if (splitor.length >= 6) { // 배열 길이 확인
					String startDate = splitor[0].split(" ")[0]; // 날짜 부분만 추출
					int amount = Integer.parseInt(splitor[1]);
					String description = splitor[2];
					String nextPayment = splitor[3].split(" ")[0]; // 다음 결제일 날짜 부분만 추출
					String frequency = splitor[4]; // MONTHLY, YEARLY
					String isActive = splitor[5]; // T/F

					String formattedAmount = String.format("%,d", amount);
					String status = isActive.equals("T") ? "Active" : "Inactive";

					// 통계 계산
					totalEntries++;
					if (frequency.equals("MONTHLY")) {
						totalMonthlyAmount += amount;
					} else if (frequency.equals("YEARLY")) {
						totalYearlyAmount += amount;
					}

					// 카테고리별 분류 (구독 서비스명 기준)
					categoryTotals.put(description, categoryTotals.getOrDefault(description, 0) + amount);
					frequencyTotals.put(frequency, frequencyTotals.getOrDefault(frequency, 0) + amount);

					// 구독 상세 정보 저장
					subscriptionDetails.put(description, String.format("%s KRW (%s)", formattedAmount, frequency));

					res.append(String.format("%2d. Service: %s\n", i + 1, description));
					res.append(String.format("    Amount: %s KRW | Frequency: %s | Status: %s\n", formattedAmount,
							frequency, status));
					res.append(String.format("    Start Date: %s | Next Payment: %s\n\n", startDate, nextPayment));
				}
			}

			// 월간 환산 총액 계산 (연간 구독료를 12로 나눔)
			int monthlyEquivalent = totalMonthlyAmount + (totalYearlyAmount / 12);
			int yearlyEquivalent = (totalMonthlyAmount * 12) + totalYearlyAmount;

			// 계산된 통계 정보 추가
			res.append("\n📊 CALCULATED STATISTICS (DO NOT MODIFY):\n");
			res.append("- Total Subscriptions: ").append(totalEntries).append("\n");
			res.append("- Monthly Subscriptions Total: ").append(String.format("%,d", totalMonthlyAmount))
					.append(" KRW\n");
			res.append("- Yearly Subscriptions Total: ").append(String.format("%,d", totalYearlyAmount))
					.append(" KRW\n");
			res.append("- Monthly Equivalent (All): ").append(String.format("%,d", monthlyEquivalent)).append(" KRW\n");
			res.append("- Yearly Equivalent (All): ").append(String.format("%,d", yearlyEquivalent)).append(" KRW\n");
			res.append("- Average per Subscription: ")
					.append(String.format("%,d", monthlyEquivalent / Math.max(1, totalEntries))).append(" KRW/month\n");

			// 수입 대비 구독 지출 비율 계산 (수입 데이터가 있는 경우)
			if (getimpDTA != null && !getimpDTA.isEmpty()) {
				int totalIncome = 0;
				for (String getDTA : getimpDTA) {
					String[] userimportDTA = getDTA.split(",");
					if (userimportDTA.length >= 1) {
						int incomeAmount = Integer.parseInt(userimportDTA[0].replaceAll("[^0-9]", ""));
						totalIncome += incomeAmount;
					}
				}

				if (totalIncome > 0) {
					double subscriptionRatio = ((double) monthlyEquivalent / totalIncome) * 100;
					res.append("\n💸 Income vs Subscription Analysis:\n");
					res.append("- Monthly Income: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
					res.append("- Monthly Subscription Cost: ").append(String.format("%,d", monthlyEquivalent))
							.append(" KRW\n");
					res.append("- Subscription Ratio: ").append(String.format("%.2f", subscriptionRatio))
							.append("% of income\n");

					// 권장 비율과 비교
					if (subscriptionRatio > 15) {
						res.append("- Status: HIGH - Consider reviewing subscription portfolio\n");
					} else if (subscriptionRatio > 10) {
						res.append("- Status: MODERATE - Monitor subscription growth\n");
					} else {
						res.append("- Status: REASONABLE - Well-managed subscription spending\n");
					}
				}
			}

			res.append("\n📈 Category Breakdown:\n");
			for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
				double percentage = (double) entry.getValue() / (totalMonthlyAmount + totalYearlyAmount) * 100;
				res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue()))
						.append(" KRW (").append(String.format("%.1f", percentage)).append("%)\n");
			}

			res.append("\n📅 Payment Frequency Breakdown:\n");
			for (Map.Entry<String, Integer> entry : frequencyTotals.entrySet()) {
				double percentage = (double) entry.getValue() / (totalMonthlyAmount + totalYearlyAmount) * 100;
				res.append("  - ").append(entry.getKey()).append(": ").append(String.format("%,d", entry.getValue()))
						.append(" KRW (").append(String.format("%.1f", percentage)).append("%)\n");
			}

			res.append("\n💰 Subscription Services List:\n");
			for (Map.Entry<String, String> entry : subscriptionDetails.entrySet()) {
				res.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
			}

			// 강화된 분석 지침 (수입 정보 포함)
			res.append("\n🚨 CRITICAL ANALYSIS RULES:\n");
			res.append("1. NEVER create, assume, or add subscription or income data not explicitly shown above\n");
			res.append("2. NEVER modify any amounts, dates, or service names\n");
			res.append("3. NEVER perform calculations different from the provided statistics\n");
			res.append("4. USE the income vs subscription ratio provided above for affordability analysis\n");
			res.append("5. CONSIDER user's financial capacity based on income data\n");
			res.append("6. NEVER assume subscription details beyond what's shown\n");
			res.append("7. If you cannot analyze something due to insufficient data, explicitly state this\n");
			res.append("8. Use ONLY the calculated statistics provided above\n");
			res.append("9. Focus on income-based subscription management and optimization\n");
			res.append("10. Consider financial health based on subscription-to-income ratio\n");

			// 분석 요청 항목 (수입 기반 구독 분석)
			res.append("\n📝 Income-Based Subscription Analysis Requirements:\n");
			res.append("Based ONLY on the income and subscription data above, provide:\n");
			res.append("- Income vs subscription spending analysis\n");
			res.append("- Affordability assessment based on income ratio\n");
			res.append("- Subscription optimization considering financial capacity\n");
			res.append("- Budget allocation recommendations\n");
			res.append("- Risk assessment of current subscription levels\n");
			res.append("- Prioritization guidance for subscription management\n");

			res.append("\n✅ INCOME-AWARE ANALYSIS FOCUS:\n");
			res.append("- Subscription affordability within income constraints\n");
			res.append("- Financial health indicators (subscription ratio)\n");
			res.append("- Income-proportionate subscription recommendations\n");
			res.append("- Emergency fund impact consideration\n");
			res.append("- Long-term financial sustainability\n");

		} else {
			// 구독 데이터가 없을 경우
			res.append("⚠️ No expense data was found for the requested period.\n");
			res.append("There may have been no transactions, or the data has not yet been recorded.\n\n");
			res.append("💡 Next Steps:\n");
			res.append("- Try selecting a different week or category.\n");
			res.append("- Ensure expenses are correctly logged in the system.\n");
			res.append("- You can request help with tracking or input formatting.\n");
			res.append("- Contact support if you believe this is an error.\n");
			res.append(
					"Since there is no data currently retrieved, please respond with a message indicating that no data is available.");

			// 수입 데이터만 있는 경우의 분석 가이드
			if (getimpDTA != null && !getimpDTA.isEmpty()) {
				res.append("💡 Income-Based Recommendations:\n");
				res.append("- Consider setting up subscription tracking\n");
				res.append("- Plan subscription budget based on available income\n");
				res.append("- Establish subscription spending limits\n");
			}
		}

		return res.toString();
	}

	// 이번달 지출 & 지난달 지출 데이터 가져와서 변동 추이 분석요청을 위해 프롬프트로 가공하는 메서드
	public String getMonthlyTrendAnalysisData(String userinput, String userinfo) {
		List<String> getMonthlyData = lr.getMonthlyExpenseSummary(userinfo); // 사용자의 월 지출 데이터
		List<String> getLateData = lr.getLateMonthlyExpenseSummary(userinfo); // 사용자의 지난달 지출 데이터
		List<String> getimpDTA = lr.getInspensiveData(userinfo); // 사용자의 월 수입 데이터

		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		// 수입 데이터 처리
		int totalIncome = 0;
		if (getimpDTA != null && !getimpDTA.isEmpty()) {
			res.append("💰 User Income Information:\n");
			int incomeCount = 0;

			for (int j = 0; j < getimpDTA.size(); j++) {
				String getDTA = getimpDTA.get(j);
				String[] userimportDTA = getDTA.split(",");

				if (userimportDTA.length >= 4) {
					String amount = userimportDTA[0];
					String incomeType = userimportDTA[1];
					String paymentDate = userimportDTA[2];
					String nextPaymentDate = userimportDTA[3];

					int incomeAmount = Integer.parseInt(amount.replaceAll("[^0-9]", ""));
					totalIncome += incomeAmount;
					incomeCount++;

					String formattedIncome = String.format("%,d", incomeAmount);

					res.append(String.format("%2d. Income Type: %s\n", j + 1, incomeType));
					res.append(String.format("    Amount: %s KRW\n", formattedIncome));
					res.append(String.format("    Payment Date: %s\n", paymentDate));
					res.append(String.format("    Next Payment: %s\n\n", nextPaymentDate));
				}
			}

			res.append("📊 Income Summary:\n");
			res.append("- Total Monthly Income: ").append(String.format("%,d", totalIncome)).append(" KRW\n");
			res.append("- Number of Income Sources: ").append(incomeCount).append("\n");
			res.append("- Average per Source: ").append(String.format("%,d", totalIncome / Math.max(1, incomeCount)))
					.append(" KRW\n\n");
		} else {
			res.append("⚠️ No income data available.\n\n");
		}

		// 이번달 지출 데이터 처리
		int currentMonthTotal = 0;
		Map<String, Integer> currentMonthCategories = new HashMap<>();
		String currentPeriod = "";

		if (getMonthlyData != null && !getMonthlyData.isEmpty()) {
			res.append("📅 Current Month Expense Data:\n");

			for (int i = 0; i < getMonthlyData.size(); i++) {
				String expenseData = getMonthlyData.get(i);
				String[] expenseInfo = expenseData.split(",");

				if (expenseInfo.length >= 4) {
					String dateRange = expenseInfo[0]; // 월간범위
					String expenseDate = expenseInfo[1]; // 지출일
					String category = expenseInfo[2]; // 지출카테고리
					String amountStr = expenseInfo[3]; // 지출가격

					if (i == 0) {
						currentPeriod = dateRange;
					}

					int amount = Integer.parseInt(amountStr.replaceAll("[^0-9]", ""));
					currentMonthTotal += amount;

					// 카테고리별 집계
					currentMonthCategories.put(category, currentMonthCategories.getOrDefault(category, 0) + amount);

					String formattedAmount = String.format("%,d", amount);

					if (i == 0) {
						res.append("Period: ").append(dateRange).append("\n\n");
					}

					// 상위 15개만 표시
					if (i < 15) {
						res.append(String.format("%3d. Date: %s | Category: %s | Amount: %s KRW\n", i + 1, expenseDate,
								category, formattedAmount));
					}
				}
			}

			if (getMonthlyData.size() > 15) {
				res.append("... (").append(getMonthlyData.size() - 15).append(" more transactions)\n");
			}

			res.append("\n📊 Current Month Summary:\n");
			res.append("- Total Expenses: ").append(String.format("%,d", currentMonthTotal)).append(" KRW\n");
			res.append("- Number of Transactions: ").append(getMonthlyData.size()).append("\n");
			res.append("- Average per Transaction: ")
					.append(String.format("%,d", currentMonthTotal / Math.max(1, getMonthlyData.size())))
					.append(" KRW\n\n");

		} else {
			res.append("⚠️ No current month expense data available.\n\n");
		}

		// 지난달 지출 데이터 처리
		int lastMonthTotal = 0;
		Map<String, Integer> lastMonthCategories = new HashMap<>();
		String lastPeriod = "";

		if (getLateData != null && !getLateData.isEmpty()) {
			res.append("📅 Previous Month Expense Data:\n");

			for (int i = 0; i < getLateData.size(); i++) {
				String expenseData = getLateData.get(i);
				String[] expenseInfo = expenseData.split(",");

				if (expenseInfo.length >= 4) {
					String amount = expenseInfo[0]; // 사용금액
					String category = expenseInfo[1]; // 사용처
					String expenseDate = expenseInfo[2]; // 지출일
					String dateRange = expenseInfo[3]; // 월간범위

					if (i == 0) {
						lastPeriod = dateRange;
					}

					int expenseAmount = Integer.parseInt(amount.replaceAll("[^0-9]", ""));
					lastMonthTotal += expenseAmount;

					// 카테고리별 집계
					lastMonthCategories.put(category, lastMonthCategories.getOrDefault(category, 0) + expenseAmount);

					String formattedAmount = String.format("%,d", expenseAmount);

					if (i == 0) {
						res.append("Period: ").append(dateRange).append("\n\n");
					}

					// 상위 15개만 표시
					if (i < 15) {
						res.append(String.format("%3d. Date: %s | Category: %s | Amount: %s KRW\n", i + 1, expenseDate,
								category, formattedAmount));
					}
				}
			}

			if (getLateData.size() > 15) {
				res.append("... (").append(getLateData.size() - 15).append(" more transactions)\n");
			}

			res.append("\n📊 Previous Month Summary:\n");
			res.append("- Total Expenses: ").append(String.format("%,d", lastMonthTotal)).append(" KRW\n");
			res.append("- Number of Transactions: ").append(getLateData.size()).append("\n");
			res.append("- Average per Transaction: ")
					.append(String.format("%,d", lastMonthTotal / Math.max(1, getLateData.size()))).append(" KRW\n\n");

		} else {
			res.append("⚠️ No previous month expense data available.\n\n");
		}

		// 🚀 핵심: 월별 지출 변동 추이 분석
		if (currentMonthTotal > 0 && lastMonthTotal > 0) {
			res.append("🔍 MONTH-TO-MONTH SPENDING TREND ANALYSIS:\n");
			res.append("==============================================\n\n");

			int expenseDifference = currentMonthTotal - lastMonthTotal;
			double changePercentage = ((double) expenseDifference / lastMonthTotal) * 100;

			res.append("📈 Overall Spending Trend:\n");
			res.append("- Previous Month (").append(lastPeriod).append("): ")
					.append(String.format("%,d", lastMonthTotal)).append(" KRW\n");
			res.append("- Current Month (").append(currentPeriod).append("): ")
					.append(String.format("%,d", currentMonthTotal)).append(" KRW\n");
			res.append("- Net Change: ").append(String.format("%+,d", expenseDifference)).append(" KRW\n");
			res.append("- Percentage Change: ").append(String.format("%+.2f", changePercentage)).append("%\n");

			// 변동 추이 등급화
			String trendStatus = "";
			String trendEmoji = "";
			if (Math.abs(changePercentage) < 5) {
				trendStatus = "STABLE - 안정적인 지출 패턴";
				trendEmoji = "➡️";
			} else if (changePercentage >= 20) {
				trendStatus = "SHARP INCREASE - 급격한 지출 증가";
				trendEmoji = "📈🔥";
			} else if (changePercentage >= 10) {
				trendStatus = "MODERATE INCREASE - 지출 증가 추세";
				trendEmoji = "📈";
			} else if (changePercentage >= 5) {
				trendStatus = "SLIGHT INCREASE - 소폭 지출 증가";
				trendEmoji = "⬆️";
			} else if (changePercentage <= -20) {
				trendStatus = "SHARP DECREASE - 급격한 지출 감소";
				trendEmoji = "📉💚";
			} else if (changePercentage <= -10) {
				trendStatus = "MODERATE DECREASE - 지출 감소 추세";
				trendEmoji = "📉";
			} else if (changePercentage <= -5) {
				trendStatus = "SLIGHT DECREASE - 소폭 지출 감소";
				trendEmoji = "⬇️";
			}

			res.append("- Trend Classification: ").append(trendEmoji).append(" ").append(trendStatus).append("\n\n");

			// 카테고리별 변동 상세 분석
			res.append("🔄 CATEGORY-WISE CHANGE ANALYSIS:\n");
			Set<String> allCategories = new HashSet<>(currentMonthCategories.keySet());
			allCategories.addAll(lastMonthCategories.keySet());

			// 변동이 큰 카테고리 순으로 정렬
			List<Map.Entry<String, Integer>> categoryChanges = new ArrayList<>();
			for (String category : allCategories) {
				int currentAmount = currentMonthCategories.getOrDefault(category, 0);
				int lastAmount = lastMonthCategories.getOrDefault(category, 0);
				int categoryDiff = currentAmount - lastAmount;
				categoryChanges.add(new AbstractMap.SimpleEntry<>(category, categoryDiff));
			}

			// 절댓값 기준으로 정렬 (변동이 큰 순서)
			categoryChanges.sort((a, b) -> Integer.compare(Math.abs(b.getValue()), Math.abs(a.getValue())));

			res.append("Changes sorted by impact (largest changes first):\n\n");
			for (Map.Entry<String, Integer> entry : categoryChanges) {
				String category = entry.getKey();
				int categoryDiff = entry.getValue();
				int currentAmount = currentMonthCategories.getOrDefault(category, 0);
				int lastAmount = lastMonthCategories.getOrDefault(category, 0);

				if (lastAmount > 0 && categoryDiff != 0) {
					double categoryChangePercentage = ((double) categoryDiff / lastAmount) * 100;

					String changeType = "";
					String changeIcon = "";
					if (Math.abs(categoryChangePercentage) >= 50) {
						changeType = "MAJOR CHANGE";
						changeIcon = categoryDiff > 0 ? "🔴" : "🟢";
					} else if (Math.abs(categoryChangePercentage) >= 25) {
						changeType = "SIGNIFICANT CHANGE";
						changeIcon = categoryDiff > 0 ? "🟡" : "🟢";
					} else if (Math.abs(categoryChangePercentage) >= 10) {
						changeType = "MODERATE CHANGE";
						changeIcon = categoryDiff > 0 ? "⬆️" : "⬇️";
					} else {
						changeType = "MINOR CHANGE";
						changeIcon = categoryDiff > 0 ? "↗️" : "↘️";
					}

					res.append(String.format("%s %s:\n", changeIcon, category));
					res.append(String.format("   Previous: %,d KRW → Current: %,d KRW\n", lastAmount, currentAmount));
					res.append(String.format("   Change: %+,d KRW (%+.1f%%) - %s\n\n", categoryDiff,
							categoryChangePercentage, changeType));

				} else if (currentAmount > 0 && lastAmount == 0) {
					res.append(String.format("🆕 %s: +%,d KRW (NEW CATEGORY)\n\n", category, currentAmount));
				} else if (lastAmount > 0 && currentAmount == 0) {
					res.append(String.format("❌ %s: -%,d KRW (CATEGORY REMOVED)\n\n", category, lastAmount));
				}
			}

			// 지출 패턴 변화 요약
			res.append("📊 SPENDING PATTERN CHANGE SUMMARY:\n");
			int increasedCategories = (int) categoryChanges.stream().filter(e -> e.getValue() > 0).count();
			int decreasedCategories = (int) categoryChanges.stream().filter(e -> e.getValue() < 0).count();
			int stableCategories = (int) categoryChanges.stream().filter(e -> e.getValue() == 0).count();

			res.append("- Categories with increased spending: ").append(increasedCategories).append("\n");
			res.append("- Categories with decreased spending: ").append(decreasedCategories).append("\n");
			res.append("- Categories with stable spending: ").append(stableCategories).append("\n\n");

			// 트랜잭션 빈도 변화
			int currentTransactions = getMonthlyData != null ? getMonthlyData.size() : 0;
			int lastTransactions = getLateData != null ? getLateData.size() : 0;
			int transactionDiff = currentTransactions - lastTransactions;

			res.append("💳 TRANSACTION FREQUENCY TREND:\n");
			res.append("- Previous Month Transactions: ").append(lastTransactions).append("\n");
			res.append("- Current Month Transactions: ").append(currentTransactions).append("\n");
			res.append("- Transaction Change: ").append(String.format("%+d", transactionDiff)).append("\n");

			if (currentTransactions > 0 && lastTransactions > 0) {
				int currentAvg = currentMonthTotal / currentTransactions;
				int lastAvg = lastMonthTotal / lastTransactions;
				int avgDiff = currentAvg - lastAvg;
				double avgChangePercentage = ((double) avgDiff / lastAvg) * 100;

				res.append("- Average Amount Change: ").append(String.format("%+,d", avgDiff)).append(" KRW (")
						.append(String.format("%+.1f", avgChangePercentage)).append("%)\n\n");
			}

		} else {
			res.append("⚠️ Insufficient data for trend analysis (need both months' data).\n\n");
		}

		// 수입 대비 지출 변동 분석
		if (totalIncome > 0) {
			res.append("💸 INCOME vs EXPENSE TREND ANALYSIS:\n");

			if (currentMonthTotal > 0) {
				double currentExpenseRatio = ((double) currentMonthTotal / totalIncome) * 100;
				res.append("- Current Month Expense Ratio: ").append(String.format("%.2f", currentExpenseRatio))
						.append("% of income\n");
			}

			if (lastMonthTotal > 0) {
				double lastExpenseRatio = ((double) lastMonthTotal / totalIncome) * 100;
				res.append("- Previous Month Expense Ratio: ").append(String.format("%.2f", lastExpenseRatio))
						.append("% of income\n");

				if (currentMonthTotal > 0) {
					double currentExpenseRatio = ((double) currentMonthTotal / totalIncome) * 100;
					double ratioChange = currentExpenseRatio - lastExpenseRatio;
					res.append("- Expense Ratio Change: ").append(String.format("%+.2f", ratioChange))
							.append(" percentage points\n");
				}
			}

			int remainingBudget = totalIncome - currentMonthTotal;
			res.append("- Remaining Budget: ").append(String.format("%,d", remainingBudget)).append(" KRW\n\n");
		}

		// 분석 규칙 및 지침 - HTML 포맷팅 요청 추가
		res.append("🚨 CRITICAL ANALYSIS RULES:\n");
		res.append("1. NEVER create, assume, or add expense or income data not explicitly shown above\n");
		res.append("2. NEVER modify any amounts, dates, or category names\n");
		res.append("3. NEVER perform calculations different from the provided statistics\n");
		res.append("4. USE ONLY the calculated trend data and percentages provided above\n");
		res.append("5. FOCUS on month-to-month changes and spending behavior evolution\n");
		res.append("6. NEVER assume expense details beyond what's shown\n");
		res.append("7. If you cannot analyze something due to insufficient data, explicitly state this\n");
		res.append("8. PRIORITIZE trend analysis over absolute amounts\n");
		res.append("9. Consider spending pattern shifts and behavioral changes\n");
		res.append("10. Focus on actionable insights from trend data\n");

		// HTML 포맷팅 요청
		res.append("\n🎨 RESPONSE FORMATTING REQUIREMENTS:\n");
		res.append("CRITICAL: Format your analysis response using HTML tags for better readability:\n");
		res.append("- Use <h3> for main section headers\n");
		res.append("- Use <h4> for sub-section headers\n");
		res.append("- Use <ul> and <li> for lists and bullet points\n");
		res.append("- Use <br> for line breaks within paragraphs\n");
		res.append("- Use <hr> to separate major sections\n");
		res.append("- Use <strong> for emphasis on important figures\n");
		res.append("- Use <em> for trend descriptions\n");
		res.append("- Use <div> with appropriate styling classes if needed\n");
		res.append("- Structure your response with clear visual hierarchy\n");
		res.append("- Make the analysis easy to scan and read\n");

		// 분석 요청 항목
		res.append("\n📝 MONTHLY TREND ANALYSIS REQUIREMENTS:\n");
		res.append("Based ONLY on the expense trend data above, provide HTML-formatted analysis of:\n");
		res.append("- Overall spending trend direction and magnitude\n");
		res.append("- Category-wise spending changes and their significance\n");
		res.append("- Spending behavior evolution analysis\n");
		res.append("- Transaction frequency and average amount trends\n");
		res.append("- Budget management trend assessment\n");
		res.append("- Financial discipline indicators\n");
		res.append("- Actionable recommendations based on trends\n");

		res.append("\n✅ TREND ANALYSIS FOCUS:\n");
		res.append("- IS spending increasing, decreasing, or stable?\n");
		res.append("- WHICH categories drive the spending changes?\n");
		res.append("- HOW significant are the changes (minor/moderate/major)?\n");
		res.append("- WHAT do the trends suggest about spending habits evolution?\n");
		res.append("- ARE there concerning patterns or positive improvements?\n");
		res.append("- WHAT specific actions should be considered based on trends?\n");
		res.append("- HOW sustainable are the current spending patterns?\n");

		return res.toString();
	}

	// 주 소비처 분석 프롬프트
	public String getAmtAndDecData(String userinput, String userinfo) {
		List<String> getDDTA = lr.getAMTandDEC(userinfo); // 사용자의 전체 지출 테이블 중 [지출처, 지출 시기]로 추출
		List<String> getLateData = lr.getLateMonthlyExpenseSummary(userinfo); // 사용자의 지난달 지출 데이터
		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		// 지출처별 데이터 분석
		Map<String, Integer> categoryCount = new HashMap<>();
		Map<String, List<String>> categoryDates = new HashMap<>();
		Map<String, Integer> monthlyCategories = new HashMap<>();

		int totalTransactions = 0;
		String earliestDate = null;
		String latestDate = null;

		if (getDDTA != null && !getDDTA.isEmpty()) {
			res.append("🛍️ Spending Location Analysis:\n");
			res.append("Total Transaction Records: ").append(getDDTA.size()).append("\n\n");

			for (int i = 0; i < getDDTA.size(); i++) {
				String expenseRecord = getDDTA.get(i);
				String[] expenseInfo = expenseRecord.split(",");

				if (expenseInfo.length >= 2) {
					String category = expenseInfo[0].trim(); // 지출처 (DEC)
					String expenseDate = expenseInfo[1].trim(); // 지출 시기

					totalTransactions++;

					// 카테고리별 집계
					categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);

					// 카테고리별 날짜 리스트 저장
					categoryDates.computeIfAbsent(category, k -> new ArrayList<>()).add(expenseDate);

					// 월별 카테고리 이용 집계 (YYYY-MM 형식으로 가정)
					if (expenseDate.length() >= 7) {
						String monthKey = expenseDate.substring(0, 7) + "_" + category; // 월별-카테고리별
						monthlyCategories.put(monthKey, monthlyCategories.getOrDefault(monthKey, 0) + 1);
					}

					// 최초/최종 날짜 추적
					if (earliestDate == null || expenseDate.compareTo(earliestDate) < 0) {
						earliestDate = expenseDate;
					}
					if (latestDate == null || expenseDate.compareTo(latestDate) > 0) {
						latestDate = expenseDate;
					}

					// 처음 15개 레코드만 상세 표시
					if (i < 15) {
						res.append(String.format("%3d. %s | %s\n", i + 1, category, expenseDate));
					}
				}
			}

			if (getDDTA.size() > 15) {
				res.append("... (").append(getDDTA.size() - 15).append(" more records)\n");
			}

			res.append("\n📊 Overall Spending Location Statistics:\n");
			res.append("- Analysis Period: ").append(earliestDate != null ? earliestDate : "N/A").append(" ~ ")
					.append(latestDate != null ? latestDate : "N/A").append("\n");
			res.append("- Total Transactions: ").append(totalTransactions).append("\n");
			res.append("- Number of Different Locations: ").append(categoryCount.size()).append("\n\n");

			// 카테고리별 방문 빈도 분석 (방문 횟수 기준 상위)
			res.append("🏆 MOST FREQUENTLY VISITED PLACES:\n");
			List<Map.Entry<String, Integer>> sortedByFrequency = categoryCount.entrySet().stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue(reverseOrder())).collect(Collectors.toList());

			for (Map.Entry<String, Integer> entry : sortedByFrequency) {
				String category = entry.getKey();
				int visitCount = entry.getValue();
				double frequencyPercentage = (double) visitCount / totalTransactions * 100;

				res.append(String.format("%s:\n", category));
				res.append(String.format("   Visit Frequency: %d times (%.1f%% of all transactions)\n", visitCount,
						frequencyPercentage));

				// 방문 패턴 분석
				List<String> dates = categoryDates.get(category);
				if (dates != null && dates.size() > 1) {
					String firstVisit = dates.stream().min(String::compareTo).orElse("N/A");
					String lastVisit = dates.stream().max(String::compareTo).orElse("N/A");
					res.append(String.format("   Usage Period: %s ~ %s\n", firstVisit, lastVisit));

					// 방문 규칙성 분석
					if (visitCount >= 5) {
						res.append("   Pattern: Regular customer (5+ visits)\n");
					} else if (visitCount >= 3) {
						res.append("   Pattern: Frequent visitor (3-4 visits)\n");
					} else {
						res.append("   Pattern: Occasional visitor (1-2 visits)\n");
					}
				}
				res.append("\n");
			}

			// 카테고리 다양성 분석
			res.append("🎯 SPENDING DIVERSIFICATION ANALYSIS:\n");

			double top5Percentage = sortedByFrequency.stream().limit(5).mapToInt(Map.Entry::getValue).sum() * 100.0
					/ totalTransactions;

			res.append(String.format("- Top 5 Places: %.1f%% of total visits\n", top5Percentage));

			if (top5Percentage > 80) {
				res.append("- Diversification Level: 낮음 - 특정 장소에 집중된 소비 패턴\n");
			} else if (top5Percentage > 60) {
				res.append("- Diversification Level: 중간 - 주요 장소 중심의 소비\n");
			} else {
				res.append("- Diversification Level: 높음 - 다양한 장소에 분산된 소비\n");
			}

			// 소비 장소 카테고리 분석
			res.append("\n💡 SPENDING LOCATION INSIGHTS:\n");
			int regularPlaces = (int) categoryCount.values().stream().filter(count -> count >= 5).count();
			int frequentPlaces = (int) categoryCount.values().stream().filter(count -> count >= 3 && count < 5).count();
			int occasionalPlaces = (int) categoryCount.values().stream().filter(count -> count < 3).count();

			res.append(String.format("- Regular Places (5+ visits): %d locations\n", regularPlaces));
			res.append(String.format("- Frequent Places (3-4 visits): %d locations\n", frequentPlaces));
			res.append(String.format("- Occasional Places (1-2 visits): %d locations\n", occasionalPlaces));

			if (regularPlaces > 0) {
				res.append("- Shopping Behavior: 단골 장소가 있는 규칙적 소비자\n");
			} else if (frequentPlaces > occasionalPlaces) {
				res.append("- Shopping Behavior: 선호 장소 중심의 소비자\n");
			} else {
				res.append("- Shopping Behavior: 다양한 장소를 탐색하는 소비자\n");
			}
			res.append("\n");

		} else {
			res.append("⚠️ No spending location data available for analysis.\n\n");
		}

		// 분석 규칙 및 지침
		res.append("🚨 CRITICAL ANALYSIS RULES:\n");
		res.append("1. NEVER create, assume, or add location data not explicitly shown above\n");
		res.append("2. NEVER modify any dates or location names\n");
		res.append("3. NEVER assume specific merchant details beyond the category names shown\n");
		res.append("4. USE ONLY the visit frequency and pattern data provided above\n");
		res.append("5. FOCUS on WHERE user spends money, not HOW MUCH\n");
		res.append("6. Analyze spending location preferences and visit patterns\n");
		res.append("7. If you cannot analyze something due to insufficient data, explicitly state this\n");
		res.append("8. Focus on location diversity and visit frequency patterns\n");
		res.append("9. Consider user's shopping behavior based on place preferences\n");
		res.append("10. Evaluate location loyalty vs exploration tendency\n");

		// 분석 요청 항목
		res.append("\n📝 Location-Based Spending Analysis Requirements:\n");
		res.append("Based ONLY on the spending location data above, provide:\n");
		res.append("- Primary spending locations identification\n");
		res.append("- Visit frequency pattern analysis\n");
		res.append("- Location preference evaluation (regular vs occasional)\n");
		res.append("- Spending diversification assessment\n");
		res.append("- Location loyalty analysis\n");
		res.append("- Shopping behavior pattern identification\n");
		res.append("- Place preference categorization\n");

		res.append("\n✅ LOCATION ANALYSIS FOCUS:\n");
		res.append("- WHERE does the user spend money most frequently?\n");
		res.append("- WHICH places are regular vs occasional destinations?\n");
		res.append("- HOW diverse are the user's spending locations?\n");
		res.append("- WHAT does location preference reveal about lifestyle?\n");
		res.append("- IS the user loyal to specific places or exploratory?\n");
		res.append("- WHICH locations show consistent usage patterns?\n");
		res.append("- WHAT shopping behavior patterns emerge from location data?\n");

		return res.toString();
	}

	// 지출 빈도 추이 분석 지난달 대비 이번달
	public String getCNTInfoCurrentAndLatelyMonth(String userinput, String userinfo) {
		List<String> getCurrent = lr.CurrentMonthJichulAndFullPrice(userinfo);
		List<String> getLately = lr.LateMonthjichulcountAndFullPrice(userinfo);
		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		res.append("📊 SPENDING FREQUENCY TREND ANALYSIS (CURRENT vs LAST MONTH)\n\n");

		// 이번달 데이터 분석
		if (getCurrent != null && !getCurrent.isEmpty()) {
			res.append("📈 CURRENT MONTH ANALYSIS:\n");
			res.append("Total Category Records: ").append(getCurrent.size()).append("\n");

			int totalCurrentTransactions = 0;
			long totalCurrentAmount = 0;

			res.append("\nCurrent Month Details:\n");
			for (int i = 0; i < Math.min(getCurrent.size(), 10); i++) {
				String record = getCurrent.get(i);
				String[] parts = record.split(",");
				if (parts.length >= 2) {
					try {
						int count = Integer.parseInt(parts[0].trim());
						long amount = Long.parseLong(parts[1].trim());
						totalCurrentTransactions += count;
						totalCurrentAmount += amount;
						res.append(
								String.format("%2d. Frequency: %d times | Total Amount: %,d원\n", i + 1, count, amount));
					} catch (NumberFormatException e) {
						res.append(String.format("%2d. %s\n", i + 1, record));
					}
				}
			}

			if (getCurrent.size() > 10) {
				res.append("... (").append(getCurrent.size() - 10).append(" more records)\n");
			}

			res.append(String.format("\n📊 Current Month Summary:\n"));
			res.append(String.format("- Total Transaction Frequency: %d times\n", totalCurrentTransactions));
			res.append(String.format("- Total Amount: %,d원\n", totalCurrentAmount));
			if (totalCurrentTransactions > 0) {
				res.append(String.format("- Average per Transaction: %,d원\n",
						totalCurrentAmount / totalCurrentTransactions));
			}
		} else {
			res.append("⚠️ No current month data available.\n");
		}

		res.append("\n" + "=".repeat(50) + "\n");

		// 지난달 데이터 분석
		if (getLately != null && !getLately.isEmpty()) {
			res.append("📉 LAST MONTH ANALYSIS:\n");
			res.append("Total Category Records: ").append(getLately.size()).append("\n");

			int totalLastTransactions = 0;
			long totalLastAmount = 0;

			res.append("\nLast Month Details:\n");
			for (int i = 0; i < Math.min(getLately.size(), 10); i++) {
				String record = getLately.get(i);
				String[] parts = record.split(",");
				if (parts.length >= 2) {
					try {
						int count = Integer.parseInt(parts[0].trim());
						long amount = Long.parseLong(parts[1].trim());
						totalLastTransactions += count;
						totalLastAmount += amount;
						res.append(
								String.format("%2d. Frequency: %d times | Total Amount: %,d원\n", i + 1, count, amount));
					} catch (NumberFormatException e) {
						res.append(String.format("%2d. %s\n", i + 1, record));
					}
				}
			}

			if (getLately.size() > 10) {
				res.append("... (").append(getLately.size() - 10).append(" more records)\n");
			}

			res.append(String.format("\n📊 Last Month Summary:\n"));
			res.append(String.format("- Total Transaction Frequency: %d times\n", totalLastTransactions));
			res.append(String.format("- Total Amount: %,d원\n", totalLastAmount));
			if (totalLastTransactions > 0) {
				res.append(String.format("- Average per Transaction: %,d원\n", totalLastAmount / totalLastTransactions));
			}
		} else {
			res.append("⚠️ No last month data available.\n");
		}

		// 비교 분석
		res.append("\n" + "=".repeat(50) + "\n");
		res.append("🔄 COMPARATIVE TREND ANALYSIS:\n");

		if (getCurrent != null && getLately != null && !getCurrent.isEmpty() && !getLately.isEmpty()) {
			int currentSize = getCurrent.size();
			int lastSize = getLately.size();

			// 카테고리 수 변화
			int categoryChange = currentSize - lastSize;
			double categoryChangePercent = lastSize > 0 ? (double) categoryChange / lastSize * 100 : 0;

			res.append(String.format("📋 Spending Category Changes:\n"));
			res.append(String.format("- Current Month Categories: %d\n", currentSize));
			res.append(String.format("- Last Month Categories: %d\n", lastSize));
			res.append(String.format("- Change: %+d categories (%.1f%%)\n", categoryChange, categoryChangePercent));

			if (categoryChange > 0) {
				res.append("- Trend: 지출 카테고리 다양성 증가 📈\n");
			} else if (categoryChange < 0) {
				res.append("- Trend: 지출 카테고리 집중화 📉\n");
			} else {
				res.append("- Trend: 지출 카테고리 수 유지 ➡️\n");
			}
		}

		// 분석 가이드라인
		res.append("\n🚨 ANALYSIS GUIDELINES:\n");
		res.append("1. Compare spending frequency patterns between months\n");
		res.append("2. Analyze category diversity changes\n");
		res.append("3. Identify spending behavior trends\n");
		res.append("4. Focus on frequency patterns, not just amounts\n");
		res.append("5. Use ONLY the data provided above\n");
		res.append("6. Consider lifestyle changes based on frequency shifts\n");

		res.append("\n📝 FREQUENCY ANALYSIS REQUIREMENTS:\n");
		res.append("Based on the frequency data above, analyze:\n");
		res.append("- Monthly spending frequency comparison\n");
		res.append("- Category usage pattern changes\n");
		res.append("- Spending behavior trend identification\n");
		res.append("- Transaction frequency vs amount correlation\n");
		res.append("- Monthly spending habit evolution\n");

		return res.toString();
	}

	// 이번달 지출중에 고액 지출 건 분석
	public String getHighPriceData(String userinput, String userinfo) {
		List<String> getHighPrice = lr.getHighPriceinfo(userinfo);// 사용자의 이번달 고액 지출 데이터
		List<String> getLateData = lr.getLateMonthlyExpenseSummary(userinfo); // 사용자의 지난달 지출 데이터
		StringBuilder res = new StringBuilder();

		// 공통 헤더
		res.append("====================================\n");
		res.append("💬 User Request: ").append(userinput).append("\n");
		res.append("====================================\n\n");

		res.append("💰 HIGH-VALUE EXPENSE ANALYSIS (CURRENT MONTH)\n\n");

		// 고액 지출 데이터 분석
		if (getHighPrice != null && !getHighPrice.isEmpty()) {
			res.append("🔥 HIGH-VALUE EXPENSES (100,000원 이상):\n");
			res.append("Total High-Value Categories: ").append(getHighPrice.size()).append("\n\n");

			long totalHighValueAmount = 0;
			int totalHighValueCount = 0;

			res.append("High-Value Expense Details:\n");
			for (int i = 0; i < getHighPrice.size(); i++) {
				String record = getHighPrice.get(i);
				String[] parts = record.split(",");

				if (parts.length >= 3) {
					try {
						String category = parts[0].trim(); // 지출내용
						int frequency = Integer.parseInt(parts[1].trim()); // 발생횟수
						long totalAmount = Long.parseLong(parts[2].trim()); // 총금액

						totalHighValueCount += frequency;
						totalHighValueAmount += totalAmount;

						long avgAmount = totalAmount / frequency;

						res.append(String.format("%2d. %s\n", i + 1, category));
						res.append(String.format("    - Frequency: %d times\n", frequency));
						res.append(String.format("    - Total Amount: %,d원\n", totalAmount));
						res.append(String.format("    - Average per Transaction: %,d원\n", avgAmount));
						res.append("\n");
					} catch (NumberFormatException e) {
						res.append(String.format("%2d. %s (Format Error)\n", i + 1, record));
					}
				} else {
					res.append(String.format("%2d. %s (Incomplete Data)\n", i + 1, record));
				}
			}

			res.append("📊 HIGH-VALUE EXPENSE SUMMARY:\n");
			res.append(String.format("- Total High-Value Categories: %d\n", getHighPrice.size()));
			res.append(String.format("- Total High-Value Transactions: %d times\n", totalHighValueCount));
			res.append(String.format("- Total High-Value Amount: %,d원\n", totalHighValueAmount));
			if (totalHighValueCount > 0) {
				res.append(String.format("- Average High-Value Transaction: %,d원\n",
						totalHighValueAmount / totalHighValueCount));
			}

			// 고액 지출 패턴 분석
			res.append("\n🎯 HIGH-VALUE SPENDING PATTERN ANALYSIS:\n");
			if (totalHighValueCount >= 10) {
				res.append("- Spending Pattern: 빈번한 고액 지출자 (High-frequency big spender)\n");
			} else if (totalHighValueCount >= 5) {
				res.append("- Spending Pattern: 중간 빈도 고액 지출자 (Moderate high-value spender)\n");
			} else {
				res.append("- Spending Pattern: 선별적 고액 지출자 (Selective high-value spender)\n");
			}

			if (getHighPrice.size() == 1) {
				res.append("- Diversity: 집중형 고액 지출 (Concentrated high spending)\n");
			} else if (getHighPrice.size() <= 3) {
				res.append("- Diversity: 제한적 카테고리 고액 지출 (Limited category high spending)\n");
			} else {
				res.append("- Diversity: 다양한 카테고리 고액 지출 (Diversified high spending)\n");
			}

		} else {
			res.append("✅ No high-value expenses (100,000원 이상) found this month.\n");
			res.append("This indicates controlled spending within moderate ranges.\n\n");
		}

		// 지난달 전체 데이터와의 비교 참조
		res.append("=" + 50 + "\n");
		res.append("📋 CONTEXT: LAST MONTH'S OVERALL EXPENSES\n");

		if (getLateData != null && !getLateData.isEmpty()) {
			res.append("Last Month Total Records: ").append(getLateData.size()).append("\n");
			res.append("(Reference for spending behavior comparison)\n\n");

			// 지난달 데이터 샘플 (처음 5개만)
			res.append("Last Month Sample Transactions:\n");
			for (int i = 0; i < Math.min(getLateData.size(), 5); i++) {
				String record = getLateData.get(i);
				String[] parts = record.split(",");
				if (parts.length >= 4) {
					res.append(String.format("%d. Amount: %s원 | Category: %s | Date: %s\n", i + 1, parts[0].trim(),
							parts[1].trim(), parts[2].trim()));
				}
			}
			if (getLateData.size() > 5) {
				res.append("... (").append(getLateData.size() - 5).append(" more last month records)\n");
			}
		} else {
			// 데이터가 없을 경우
			res.append("⚠️ No expense data was found for the requested period.\n");
			res.append("There may have been no transactions, or the data has not yet been recorded.\n\n");
			res.append("💡 Next Steps:\n");
			res.append("- Try selecting a different week or category.\n");
			res.append("- Ensure expenses are correctly logged in the system.\n");
			res.append("- You can request help with tracking or input formatting.\n");
			res.append("- Contact support if you believe this is an error.\n");
			res.append(
					"Since there is no data currently retrieved, please respond with a message indicating that no data is available.");
		}

		// 분석 가이드라인
		res.append("\n🚨 HIGH-VALUE ANALYSIS RULES:\n");
		res.append("1. Focus on expenses 100,000원 and above only\n");
		res.append("2. Analyze frequency and total amounts of high-value categories\n");
		res.append("3. Identify high-spending patterns and behavior\n");
		res.append("4. Compare against overall spending context when available\n");
		res.append("5. Use ONLY the high-value data provided above\n");
		res.append("6. Consider financial impact and spending priorities\n");
		res.append("7. Evaluate spending control and budget management\n");

		res.append("\n📝 HIGH-VALUE EXPENSE ANALYSIS REQUIREMENTS:\n");
		res.append("Based on the high-value expense data above, provide:\n");
		res.append("- High-value spending category identification\n");
		res.append("- Frequency and amount analysis of expensive purchases\n");
		res.append("- Spending priority and pattern assessment\n");
		res.append("- Financial impact evaluation\n");
		res.append("- High-value spending behavior analysis\n");
		res.append("- Budget management insight\n");
		res.append("- Recommendations for high-value expense control\n");

		res.append("\n✅ HIGH-VALUE ANALYSIS FOCUS:\n");
		res.append("- WHICH categories have the highest individual expenses?\n");
		res.append("- HOW frequently do high-value expenses occur?\n");
		res.append("- WHAT is the total impact of high-value spending?\n");
		res.append("- IS high-value spending concentrated or diversified?\n");
		res.append("- HOW does high-value spending affect overall budget?\n");
		res.append("- WHAT patterns emerge from expensive purchase behavior?\n");
		res.append("- ARE high-value expenses planned or impulsive?\n");

		return res.toString();
	}
}
