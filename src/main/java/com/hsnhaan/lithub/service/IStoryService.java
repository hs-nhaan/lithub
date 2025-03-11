package com.hsnhaan.lithub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.hsnhaan.lithub.model.Story;

public interface IStoryService {

	List<Story> getAll();
	Story getById(int id);
	Optional<Story> getBySlug(String slug);
	List<Story> search(String keyword);
	Page<Story> getAll(int page, int limit);
	Page<Story> search(String keyword, int page, int limit);
	void save(Story story, MultipartFile file, List<Integer> genreIds, String uploadDir);
	Story update(String slug, Story story, MultipartFile file, List<Integer> genreIds, String uploadDir);
	void delete(String slug, String uploadDir);
	boolean existsById(int id);
	
}
