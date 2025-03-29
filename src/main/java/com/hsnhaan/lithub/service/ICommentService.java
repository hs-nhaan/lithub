package com.hsnhaan.lithub.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.Comment;

public interface ICommentService {
	
	List<Comment> getAll();
	Comment getById(int id);
	Page<Comment> getByStoryId(int storyId, int page, int limit);
	Page<Comment> getAll(int page, int limit);
	Page<Comment> search(int storyId, String keyword, int page, int limit);
	void save(String content, String storySlug, int userId);
	void delete(int id);
	
}
