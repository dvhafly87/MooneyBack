package com.MooneyB.Category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	
	public List<Category> findByMember_Mmemid(String memberId);
}
