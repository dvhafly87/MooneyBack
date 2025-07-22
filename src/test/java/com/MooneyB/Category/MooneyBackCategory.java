//package com.MooneyB.Category; // 적절한 패키지명으로 변경해주세요.
//
//import com.MooneyB.Member.Member; // Member 엔티티의 패키지 경로를 정확히 입력해주세요.
//import com.MooneyB.Member.MemberRepo; // MemberRepo는 Member 클래스의 리포지토리입니다.
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class MooneyBackCategory {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private MemberRepo mr;
//
//    private Member m; // 테스트에 사용할 Member 객체
//
//    // Category 객체를 생성하고 저장하는 헬퍼 메서드
//    private Category createAndSaveCategory(Member member, String name, String color) {
//        Category category = new Category();
//        category.setMcatName(name);
//        category.setMcatColor(color);
//        category.setMember(member); // Member 연결
//        return categoryRepository.save(category);
//    }
//
//    @BeforeEach
//    void setUp() {
//		Optional<Member> d = mr.findById("hhhh234");
//		if (d.isPresent()) {
//        	m = d.get();
//        } else {
//        	throw new NoSuchElementException("uiuiui234을 찾을 수 없었습니다.");
//        }
//
//        // 기존에 해당 멤버의 카테고리가 있다면 모두 삭제하여 테스트 데이터의 일관성 유지
//        List<Category> existingCategories = categoryRepository.findByMember_Mmemid(m.getMmemid());
//        if (!existingCategories.isEmpty()) {
//            categoryRepository.deleteAll(existingCategories);
//            System.out.println("기존 카테고리 데이터 " + existingCategories.size() + "개 삭제 완료.");
//        }
//
//
//        // --- 카테고리 더미 데이터 생성 ---
//        System.out.println("---- Category Setup Start for member: " + m.getMmemid() + " ----");
//
//        createAndSaveCategory(m, "식비", "#FF5733");
//        createAndSaveCategory(m, "교통비", "#33FF57");
//        createAndSaveCategory(m, "문화생활", "#3357FF");
//        createAndSaveCategory(m, "주거/통신", "#FFFF33");
//        createAndSaveCategory(m, "의료비", "#FF33FF");
//        createAndSaveCategory(m, "교육", "#33FFFF");
//        createAndSaveCategory(m, "월급", "#008000"); // 수입 카테고리
//        createAndSaveCategory(m, "용돈", "#9ACD32"); // 수입 카테고리
//        createAndSaveCategory(m, "저축/투자", "#800080");
//        createAndSaveCategory(m, "경조사", "#FFD700");
//        createAndSaveCategory(m, "기타", "#C0C0C0");
//
//        System.out.println("---- Category Setup Complete. Total Categories for " + m.getMmemid() + ": " + categoryRepository.findByMember_Mmemid(m.getMmemid()).size() + " ----");
//    }
//
//    @Test
//    @DisplayName("생성된 카테고리 데이터 전체 조회 검증")
//    void testGetAllCategoriesForUser() {
//        // Given (setUp에서 데이터 생성 완료)
//
//        // When
//        List<Category> categories = categoryRepository.findByMember_Mmemid(m.getMmemid());
//
//        // Then
//        assertNotNull(categories);
//        assertFalse(categories.isEmpty(), "카테고리 목록이 비어있으면 안 됩니다.");
//        // 총 11개의 카테고리를 생성했으므로, 사이즈를 11로 기대합니다.
//        assertEquals(11, categories.size(), "생성된 카테고리 수가 예상과 다릅니다.");
//
//        // 특정 카테고리가 제대로 들어갔는지 검증
//        boolean foodCategoryExists = categories.stream()
//                .anyMatch(c -> c.getMcatName().equals("식비") && c.getMcatColor().equals("#FF5733"));
//        assertTrue(foodCategoryExists, "식비 카테고리가 존재해야 합니다.");
//
//        boolean salaryCategoryExists = categories.stream()
//                .anyMatch(c -> c.getMcatName().equals("월급") && c.getMcatColor().equals("#008000"));
//        assertTrue(salaryCategoryExists, "월급 카테고리가 존재해야 합니다.");
//
//        System.out.println("카테고리 전체 조회 테스트 통과: " + categories.size() + "개 카테고리 확인됨.");
//    }
//}