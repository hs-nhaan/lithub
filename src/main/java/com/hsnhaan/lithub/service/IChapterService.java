package com.hsnhaan.lithub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.Chapter;

public interface IChapterService {

	Chapter getById(int id);
	Optional<Chapter> getByNumber(int chapterNumber, int storyId);
	List<Chapter> search(String keyword, int storyId);
	List<Chapter> getByStoryId(int storyId);
	Page<Chapter> search(String keyword, int storyId, int page, int limit);
	Page<Chapter> getByStoryId(int storyId, int page, int limit);
	int nextChapter(int storyId);
	void save(Chapter chapter);
	void update(Chapter chapter);
	void delete(Chapter chapter);
	
}
