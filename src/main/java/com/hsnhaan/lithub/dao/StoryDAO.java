package com.hsnhaan.lithub.dao;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.Story;

public interface StoryDAO extends JpaRepository<Story, Integer> {

	Story findBySlug(String slug);
	@Query("select distinct s from Story s join s.genres g where s.title like %:keyword% or g.name like %:keyword%")
	Page<Story> search(@Param("keyword") String keyword, Pageable pageable);
	@Query("select count(s) > 0 from Story s where s.title like :title")
	boolean existsByTitle(@Param("title") String title);
	@Query("select s from Story s where s.created_at >= :sixMonthsAgo")
	Page<Story> findNewStory(LocalDate sixMonthsAgo, Pageable pageable);
	Page<Story> findByStatusTrue(Pageable pageable);
	@Query("select s from Story s join s.genres g where g.id = :genreId")
	Page<Story> findByGenreId(int genreId, Pageable pageable);
	
}
