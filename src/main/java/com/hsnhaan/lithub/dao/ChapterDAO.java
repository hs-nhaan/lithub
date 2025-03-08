package com.hsnhaan.lithub.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hsnhaan.lithub.model.Chapter;

public interface ChapterDAO extends JpaRepository<Chapter, Integer> {

	boolean existsByTitle(String title);
	@Query("select coalesce(max(c.chapter_number), 0) + 1 from Chapter c where c.story.id = :storyId")
	int nextChapter(@Param("storyId") int storyId);
	@Query("select c from Chapter c where c.story.id = :storyId order by c.chapter_number asc")
	List<Chapter> findByStoryId(@Param("storyId") int storyId);
	@Query("select c from Chapter c where c.chapter_number = :number and c.story.id = :storyId")
	Optional<Chapter> findByChapter_number(@Param("number") int number, @Param("storyId") int storyId);
	@Query("select c from Chapter c where (concat('', c.chapter_number) = :keyword or c.title like %:keyword%) and c.story.id = :storyId")
	List<Chapter> search(@Param("keyword") String keyword, @Param("storyId") int storyId);
	
}