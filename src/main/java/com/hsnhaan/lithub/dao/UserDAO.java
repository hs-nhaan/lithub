package com.hsnhaan.lithub.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.User;

public interface UserDAO extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);
	User findByEmail(String email);
	@Query("select u from User u where u.username like %:keyword% or u.email like %:keyword%")
	List<User> search(@Param("keyword")String keyword);
	
}
