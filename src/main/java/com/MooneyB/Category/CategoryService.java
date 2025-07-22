package com.MooneyB.Category; 

import com.MooneyB.Member.Member; 
import com.MooneyB.Member.MemberRepo; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.MooneyB.common.exceptions.CategoryNotFoundException;
import com.MooneyB.common.exceptions.MemberNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepo memberRepository; 

    public CategoryService(CategoryRepository categoryRepository, MemberRepo memberRepository) {
        this.categoryRepository = categoryRepository;
        this.memberRepository = memberRepository;
    }

    public Category createCategory(Category category, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));
        category.setMember(member); 
        return categoryRepository.save(category);
    }


    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByMemberId(String memberId) {
        return categoryRepository.findByMember_Mmemid(memberId);
    }

    public Category updateCategory(Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(updatedCategory.getMcatId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + updatedCategory.getMcatId()));

        existingCategory.setMcatName(updatedCategory.getMcatName());
        existingCategory.setMcatColor(updatedCategory.getMcatColor());

        return categoryRepository.save(existingCategory);
    }


    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}