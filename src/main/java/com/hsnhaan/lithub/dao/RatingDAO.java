package com.hsnhaan.lithub.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hsnhaan.lithub.model.Rating;

public interface RatingDAO extends JpaRepository<Rating, Integer> {

	Page<Rating> findByStoryId(int storyId, Pageable pageable);
	@Query("select r from Rating r where r.story.id = :storyId and "
	+ "(concat('', r.rating) like %:keyword% or r.user.email like %:keyword% or r.user.username like %:keyword%)")
	Page<Rating> search(int storyId, String keyword, Pageable pageable);
	boolean existsByStoryIdAndUserId(int storyId, int userId);
	
}
