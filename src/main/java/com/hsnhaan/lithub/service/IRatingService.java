package com.hsnhaan.lithub.service;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.Rating;

public interface IRatingService {

	Page<Rating> getByStoryId(int storyId, int page, int limit);
	Page<Rating> search(int storyId, String keyword, int page, int limit);
	void save(int rating, int userId, String storySlug);
	void delete(int id);
	
}
