package com.hsnhaan.lithub.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.User;

public interface UserDAO extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	@Query("select u from User u where u.username like %:keyword% or u.email like %:keyword%")
	Page<User> search(@Param("keyword")String keyword, Pageable pageable);
	@Query("select u from User u where u.verification_token = :token")
	Optional<User> findByVerification_token(String token);
	
}
