package com.hsnhaan.lithub.service.Implement;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.ChapterDAO;
import com.hsnhaan.lithub.model.Chapter;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.IChapterService;
import com.hsnhaan.lithub.util.StringHelper;

@Service
public class ChapterServiceImpl implements IChapterService {

	private final ChapterDAO chapterDAO;
	private final StoryServiceImpl storySvc;
	
	@Autowired
	public ChapterServiceImpl(ChapterDAO chapterDAO, StoryServiceImpl storySvc) {
		this.chapterDAO = chapterDAO;
		this.storySvc = storySvc;
	}

	@Override
	public Chapter getById(int id) {
		return chapterDAO.findById(id).orElse(null);
	}

	@Override
	public Optional<Chapter> getByNumber(int chapterNumber, int storyId) {
		return chapterDAO.findByChapter_number(chapterNumber, storyId);
	}

	@Override
	public List<Chapter> search(String keyword, int storyId) {
		return chapterDAO.search(keyword, storyId);
	}
	
	@Override
	public List<Chapter> getByStoryId(int storyId) {
		return chapterDAO.findByStoryId(storyId);
	}

	@Override
	public Page<Chapter> search(String keyword, int storyId, int page, int limit) {
		List<Chapter> chapters = search(keyword, storyId);
		
		Pageable pageable = PageRequest.of(page - 1, limit);
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), chapters.size());
		
		List<Chapter> subChapters = chapters.subList(start, end);
		
		return new PageImpl<Chapter>(subChapters, pageable, chapters.size());
	}
	
	@Override
	public Page<Chapter> getByStoryId(int storyId, int page, int limit) {
		List<Chapter> chapters = getByStoryId(storyId);
		
		Pageable pageable = PageRequest.of(page - 1, limit);
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), chapters.size());
		
		List<Chapter> subChapters = chapters.subList(start, end);
		
		return new PageImpl<Chapter>(subChapters, pageable, chapters.size());
	}

	@Override
	public int nextChapter(int storyId) {
		return chapterDAO.nextChapter(storyId);
	}

	@Override
	public void save(String slug, Chapter chapter) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		chapter.setTitle(StringHelper.toTitleCase(chapter.getTitle()));
		chapter.setChapter_number(chapterDAO.nextChapter(story.getId()));
		chapter.setCreated_at(Instant.now());
		chapter.setStory(story);
		validate(chapter, true);
		chapterDAO.save(chapter);
	}

	@Override
	public Chapter update(int id, Chapter chapter) {
		Chapter oldChapter = chapterDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		oldChapter.setTitle(StringHelper.toTitleCase(chapter.getTitle()));
		oldChapter.setContent(chapter.getContent());
		validate(oldChapter, false);
		return chapterDAO.save(oldChapter);
	}

	@Override
	public void delete(int id) {
		Chapter chapter = chapterDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		chapterDAO.delete(chapter);
	}
	
	private void validate(Chapter chapter, boolean isNew) {
		if (!StringUtils.hasText(chapter.getTitle()))
			throw new RuntimeException("Tiêu đề chương không được để trống");
		if (!StringUtils.hasText(chapter.getContent()))
			throw new RuntimeException("Nội dung không được để trống");
		if (chapter.getStory() == null || !storySvc.existsById(chapter.getStory().getId()))
			throw new RuntimeException("Không tìm thấy truyện");
	}

}
