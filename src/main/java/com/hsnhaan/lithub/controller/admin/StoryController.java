package com.hsnhaan.lithub.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;
import com.hsnhaan.lithub.util.StringHelper;

@Controller
public class StoryController {

	private final StoryServiceImpl storySvc;
	private final GenreServiceImpl genreSvc;
	
	@Autowired
	public StoryController(StoryServiceImpl storySvc, GenreServiceImpl genreSvc) {
		this.storySvc = storySvc;
		this.genreSvc = genreSvc;
	}
	
	@GetMapping("/admin/story")
	public String list(Model model, @RequestParam(name = "page", defaultValue = "1") int page,
						@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Story> stories = null;
		if (StringUtils.hasText(keyword)) {
			stories = storySvc.search(keyword, page, Config.resultPerAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			stories = storySvc.getAll(page, Config.resultPerAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách truyện",
			"contentPage", "admin/story/list",
			"totalPage", stories.getTotalPages(),
			"currentPage", page,
			"stories", stories
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/story/add")
	public String add(Model model) {		
		List<Genre> genres = genreSvc.getAll();
		model.addAllAttributes(Map.of(
			"titlePage", "Thêm truyện",
			"contentPage", "admin/story/add",
			"genres", genres
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/story/edit/{slug}")
	public String edit(Model model, @PathVariable("slug") String slug) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAllAttributes(Map.of(
			"titlePage", story.getTitle(),
			"contentPage", "admin/story/edit",
			"genres", genreSvc.getAll(),
			"story", story
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/story/{slug}")
	public String view(Model model, @PathVariable("slug") String slug) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAllAttributes(Map.of(
			"titlePage", story.getTitle(),
			"contentPage", "admin/story/edit",
			"story", story,
			"genres", genreSvc.getAll(),
			"readOnly", true
		));
		
		return "admin/index";
	}
	
	@PostMapping("/admin/story/create")
	public String create(RedirectAttributes redirectAttributes, @ModelAttribute Story story,
							@RequestParam(name = "file", required = false) MultipartFile file,
							@RequestParam(name = "genreIds", required = false) List<Integer> genreIds,
							@Value("${upload.dir}") String uploadDir) {
		story.setSlug(StringHelper.toSlug(story.getTitle()));
		story.setTitle(StringHelper.toTitleCase(story.getTitle()));
		story.setGenres(new HashSet<Genre>(genreSvc.getByIds(genreIds)));
		story.setCreated_at(Instant.now());
		story.setViews(0);
		story.setStatus(false);
		try {
			saveCoverImage(file, uploadDir, story);
			storySvc.save(story);
			redirectAttributes.addFlashAttribute("success", "Thêm truyện thành công");
		} catch (RuntimeException ex) {
			removeCoverImage(uploadDir, story.getCover_image());
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/story/add";
	}
	
	@PostMapping("/admin/story/update/{slug}")
	public String update(RedirectAttributes redirectAttributes, @ModelAttribute Story story,
							@RequestParam(name = "file", required = false) MultipartFile file,
							@RequestParam(name = "genreIds", required = false) List<Integer> genreIds,
							@PathVariable("slug") String slug,
							@Value("${upload.dir}") String uploadDir) {
		Story oldStory = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String oldSlug = oldStory.getSlug();
		String oldCoverImage = oldStory.getCover_image();
		try {
			oldStory.setSlug(StringHelper.toSlug(story.getTitle()));
			oldStory.setTitle(StringHelper.toTitleCase(story.getTitle()));
			oldStory.setGenres(new HashSet<Genre>(genreSvc.getByIds(genreIds)));
			if (file != null && !file.isEmpty()) {
				saveCoverImage(file, uploadDir, oldStory);
				if (!oldCoverImage.equals(oldStory.getCover_image()))
					removeCoverImage(uploadDir, oldCoverImage);
			}
			storySvc.update(oldStory);
			redirectAttributes.addFlashAttribute("success", "Chỉnh sửa truyện thành công");
			return "redirect:/admin/story/edit/" + oldStory.getSlug();
		} catch (RuntimeException ex) {
			if (file != null && !file.isEmpty()) {
				removeCoverImage(uploadDir, oldStory.getCover_image());
			}
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		return  "redirect:/admin/story/edit/" + oldSlug;
	}
	
	@GetMapping("/admin/story/delete/{slug}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("slug") String slug,
							@Value("${upload.dir}") String uploadDir) {
		try {
			Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			storySvc.delete(story);
			removeCoverImage(uploadDir, story.getCover_image());
			redirectAttributes.addFlashAttribute("success", "Xóa truyện thành công");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/story";
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
