package com.hsnhaan.lithub.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.Genre;

public interface GenreDAO extends JpaRepository<Genre, Integer> {

	List<Genre> findByNameContaining(String keyword);
	Genre findBySlug(String slug);
	@Query("select count(g) > 0 from Genre g where g.name like :name")
	boolean existByName(@Param("name") String name);
	long countByIdIn(List<Integer> ids);
	
}
