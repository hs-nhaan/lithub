package com.hsnhaan.lithub.service.Implement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.StoryDAO;
import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.IStoryService;
import com.hsnhaan.lithub.util.StringHelper;

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
	public void save(Story story, MultipartFile file, List<Integer> genreIds, String uploadDir) {
		story.setSlug(StringHelper.toSlug(story.getTitle()));
		story.setTitle(StringHelper.toTitleCase(story.getTitle()));
		if (genreIds == null || genreIds.isEmpty())
			throw new RuntimeException("Phải chọn ít nhất một thể loại");
		story.setGenres(new HashSet<Genre>(genreSvc.getByIds(genreIds)));
		story.setCreated_at(Instant.now());
		story.setViews(0);
		story.setStatus(false);
		validate(story, true);
		saveCoverImage(file, uploadDir, story);
		storyDAO.save(story);
	}

	@Override
	public Story update(String slug, Story story, MultipartFile file, List<Integer> genreIds, String uploadDir) {
		Story oldStory = Optional.ofNullable(storyDAO.findBySlug(slug)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String oldCoverImage = oldStory.getCover_image();
		
		oldStory.setSlug(StringHelper.toSlug(story.getTitle()));
		oldStory.setTitle(StringHelper.toTitleCase(story.getTitle()));
		oldStory.setDescription(story.getDescription());
		if (genreIds == null || genreIds.isEmpty())
			throw new RuntimeException("Phải chọn ít nhất một thể loại");
		oldStory.setGenres(new HashSet<Genre>(genreSvc.getByIds(genreIds)));
		validate(oldStory, false);
		if (file != null && !file.isEmpty()) {
			saveCoverImage(file, uploadDir, oldStory);
			if (!oldCoverImage.equals(oldStory.getCover_image()))
				removeCoverImage(uploadDir, oldCoverImage);
		}
		return storyDAO.save(oldStory);
	}

	@Override
	public void delete(String slug, String uploadDir) {
		Story story = Optional.ofNullable(storyDAO.findBySlug(slug)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		storyDAO.delete(story);
		removeCoverImage(uploadDir, story.getCover_image());
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
	
	private void saveCoverImage(MultipartFile file, String uploadDir, Story story) {
		Path storyDir = Paths.get(uploadDir, "story");
		try {
			String fileName = story.getSlug() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
			Path filePath = storyDir.resolve(fileName);
			story.setCover_image(fileName);
			if (!Files.exists(storyDir))
				Files.createDirectories(storyDir);
			file.transferTo(filePath.toFile());
			story.setCover_image(fileName);
		} catch (IOException ex) {
			throw new RuntimeException("Có lỗi khi tải ảnh lên");
		}
	}
	
	private void removeCoverImage(String uploadDir, String coverImage) {
		Path storyDir = Paths.get(uploadDir, "story");
		Path filePath = Paths.get(storyDir.toString(), coverImage);
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
