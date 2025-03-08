package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hsnhaan.lithub.dao.StoryDAO;
import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.IStoryService;

@Service
public class StoryServiceImpl implements IStoryService {

	private final StoryDAO storyDAO;
	private final GenreServiceImpl genreSvc;
	
	@Autowired
	public StoryServiceImpl(StoryDAO storyDAO, GenreServiceImpl genreSvc) {
		this.storyDAO = storyDAO;
		this.genreSvc = genreSvc;
	}

	@Override
	public List<Story> getAll() {
		return storyDAO.findAll();
	}

	@Override
	public Story getById(int id) {
		return storyDAO.findById(id).orElse(null);
	}

	@Override
	public Optional<Story> getBySlug(String slug) {
		return Optional.ofNullable(storyDAO.findBySlug(slug));
	}

	@Override
	public List<Story> search(String keyword) {
		return storyDAO.search(keyword);
	}

	@Override
	public Page<Story> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return storyDAO.findAll(pageable);
	}

	@Override
	public Page<Story> search(String keyword, int page, int limit) {
		List<Story> stories = search(keyword);
		
		Pageable pageable = PageRequest.of(page - 1, limit);
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), stories.size());
		
		List<Story> subStories = stories.subList(start, end);
		
		return new PageImpl<Story>(subStories, pageable, stories.size());
	}

	@Override
	public void save(Story story) {
		validate(story, true);
		storyDAO.save(story);
	}

	@Override
	public void update(Story story) {
		if (!storyDAO.existsById(story.getId()))
			throw new RuntimeException("Không tìm thấy truyện");
		validate(story, false);
		storyDAO.save(story);
	}

	@Override
	public void delete(Story story) {
		if (!storyDAO.existsById(story.getId()))
			throw new RuntimeException("Không tìm thấy truyện");
		storyDAO.delete(story);
	}
	
	@Override
	public boolean existsById(int id) {
		return storyDAO.existsById(id);
	}
	
	
	private void validate(Story story, boolean isNew) {
		if (!StringUtils.hasText(story.getTitle()))
			throw new RuntimeException("Tên truyện không được để trống");
		if (isNew || !storyDAO.findById(story.getId()).map(s -> s.getTitle().equals(story.getTitle())).orElse(false))
			if (storyDAO.existsByTitle(story.getTitle()))
				throw new RuntimeException("Tên truyện đã tồn tại");
		if (story.getGenres().isEmpty())
			throw new RuntimeException("Phải có ít nhất một thể loại");
		List<Integer> genreIds = story.getGenres().stream().map(Genre:: getId).collect(Collectors.toList());
		if (genreSvc.countByIds(genreIds) < genreIds.size())
			throw new RuntimeException("Danh sách thể loại không hợp lệ");
	}
	
}
