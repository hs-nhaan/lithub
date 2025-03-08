package com.hsnhaan.lithub.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.Story;

public interface StoryDAO extends JpaRepository<Story, Integer> {

	Story findBySlug(String slug);
	@Query("select distinct s from Story s join s.genres g where s.title like %:keyword% or g.name like %:keyword%")
	List<Story> search(@Param("keyword") String keyword);
	@Query("select count(s) > 0 from Story s where s.title like :title")
	boolean existsByTitle(@Param("title") String title);
	
}
