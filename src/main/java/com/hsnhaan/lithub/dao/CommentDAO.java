package com.hsnhaan.lithub.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hsnhaan.lithub.model.Comment;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

	Page<Comment> findByStoryId(int storyId, Pageable pageable);
	@Query("select c from Comment c where c.story.id = :storyId and "
			+ "(c.user.username like %:keyword% or c.user.email like %:keyword% or c.content like %:keyword%)")
	Page<Comment> search(int storyId, String keyword, Pageable Pagepageable);
	boolean existsByStoryIdAndUserId(int storyId, int userId);
	
}
