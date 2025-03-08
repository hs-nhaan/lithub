package com.hsnhaan.lithub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.Genre;

public interface IGenreService {

	List<Genre> getAll();
	Genre getById(int id);
	List<Genre> getByIds(List<Integer> ids);
	Optional<Genre> getBySlug(String slug);
	List<Genre> search(String keyword);
	Page<Genre> getAll(int page, int limit);
	Page<Genre> search(String keyword, int page, int limit);
	long countByIds(List<Integer> ids);
	void save(Genre genre);
	void update(Genre genre);
	void delete(Genre genre);
	
}
