package com.MooneyB.Member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepo extends CrudRepository<MemberDTO, String>{
	
	@Query(value = "SELECT * "
				 + "FROM mooney_member "
				 + "where mmem_id = :id", nativeQuery = true)
	String findByMmemid(@Param("id") String findByIdCheck);
	
	@Query(value = "SELECT * "
			+ "FROM mooney_member "
			+ "where mmem_nick = :nc", nativeQuery = true)
	String findByMmemnick(@Param("nc") String findByNcCheck);
	
	
}
