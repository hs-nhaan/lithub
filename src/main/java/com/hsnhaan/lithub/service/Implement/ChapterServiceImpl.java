package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hsnhaan.lithub.dao.ChapterDAO;
import com.hsnhaan.lithub.model.Chapter;
import com.hsnhaan.lithub.service.IChapterService;

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
	public void save(Chapter chapter) {
		validate(chapter, true);
		chapterDAO.save(chapter);
	}

	@Override
	public void update(Chapter chapter) {
		if (!chapterDAO.existsById(chapter.getId()))
			throw new RuntimeException("Không tìm thấy chương truyện");
		validate(chapter, false);
		chapterDAO.save(chapter);
	}

	@Override
	public void delete(Chapter chapter) {
		if (!chapterDAO.existsById(chapter.getId()))
			throw new RuntimeException("Không tìm thấy chương truyện");
		chapterDAO.delete(chapter);
	}
	
	private void validate(Chapter chapter, boolean isNew) {
		if (!StringUtils.hasText(chapter.getTitle()))
			throw new RuntimeException("Tiêu đề chương không được để trống");
		if (!StringUtils.hasText(chapter.getContent()))
			throw new RuntimeException("Nội dung không được để trống");
		if (chapter.getStory() != null && !storySvc.existsById(chapter.getStory().getId()))
			throw new RuntimeException("Không tìm thấy truyện");
		if (isNew || !chapterDAO.findById(chapter.getId()).map(g -> g.getTitle().equals(chapter.getTitle())).orElse(false))
			if (chapterDAO.existsByTitle(chapter.getTitle()))
				throw new RuntimeException("Tiêu đề đã tồn tại");
	}

}
