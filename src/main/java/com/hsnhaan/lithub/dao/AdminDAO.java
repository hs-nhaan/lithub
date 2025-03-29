package com.hsnhaan.lithub.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hsnhaan.lithub.model.Admin;

public interface AdminDAO extends JpaRepository<Admin, Integer> {

	@Override
	@Query("select a from Admin a where a.role = 'ADMIN'")
	Page<Admin> findAll(Pageable pageable);
	Optional<Admin> findByUsername(String username);
	@Query("select a from Admin a where (a.full_name like %:keyword% or a.username like %:keyword%) "
			+ "and a.role = 'ADMIN'")
	Page<Admin> search(String keyword, Pageable pageable);
	boolean existsByUsername(String username);
	
}
