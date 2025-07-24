package com.MooneyB.Category; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.MooneyB.common.exceptions.CategoryNotFoundException;
import com.MooneyB.common.exceptions.MemberNotFoundException;

import java.util.List;

@RestController 
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    

    // ✨ 새 카테고리 생성

    @PostMapping("/member/{memberId}") // POST /categories/member/{memberId}
    public ResponseEntity<Category> createCategory(
            @PathVariable("memberId") String memberId,
            @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category, memberId);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED); // 201 Created
    }

    

    // 🔍 ID로 카테고리 조회

    @GetMapping("/{categoryId}") // GET /categories/{categoryId}
    public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK); // 200 OK
    }

    

    // 📄 회원 ID로 모든 카테고리 조회

    @GetMapping("/member/{memberId}") // GET /categories/member/{memberId}
    public ResponseEntity<List<Category>> getCategoriesByMemberId(@PathVariable("memberId") String memberId) {
        List<Category> categories = categoryService.getCategoriesByMemberId(memberId);
        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(categories, HttpStatus.OK); // 200 OK
    }

    

    // 🔄 카테고리 업데이트

    @PutMapping("/{categoryId}") // PUT /categories/{categoryId}
    public ResponseEntity<Category> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Category category) {
        category.setMcatId(categoryId);
        Category updatedCategory = categoryService.updateCategory(category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK); // 200 OK
    }

    

    // 🗑️ 카테고리 삭제

    @DeleteMapping("/{categoryId}") // DELETE /categories/{categoryId}
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    

    // 🚨 예외 처리 핸들러

    // 카테고리를 찾지 못했을 때 발생하는 예외 처리
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }

    // 회원을 찾지 못했을 때 발생하는 예외 처리 (MemberNotFoundException이 정의되어 있다고 가정)
    // import com.MooneyB.common.exceptions.MemberNotFoundException; // 예시
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    // 유효하지 않은 인자(예: 중복된 이름, 잘못된 색상 형식 등)가 전달되었을 때 발생하는 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }
}