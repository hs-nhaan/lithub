package com.hsnhaan.lithub.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hsnhaan.lithub.model.Chapter;

public interface ChapterDAO extends JpaRepository<Chapter, Integer> {

	@Query("select coalesce(max(c.chapter_number), 0) + 1 from Chapter c where c.story.id = :storyId")
	int nextChapter(int storyId);
	@Query("select c from Chapter c where c.story.id = :storyId order by c.chapter_number asc")
	Page<Chapter> findByStoryId(int storyId, Pageable pageable);
	@Query("select c from Chapter c where c.chapter_number = :number and c.story.id = :storyId")
	Optional<Chapter> findByChapter_number(int number, int storyId);
	@Query("select c from Chapter c where (concat('', c.chapter_number) like %:keyword% or c.title like %:keyword%) and c.story.id = :storyId "
			+ "order by c.chapter_number asc")
	Page<Chapter> search(String keyword, int storyId, Pageable pageable);
	
}