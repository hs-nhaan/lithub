package com.hsnhaan.lithub.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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

import jakarta.servlet.http.HttpServletRequest;

@Validated
@Controller
public class StoryController {

	private final StoryServiceImpl storySvc;
	private final GenreServiceImpl genreSvc;
	
	public StoryController(StoryServiceImpl storySvc, GenreServiceImpl genreSvc) {
		this.storySvc = storySvc;
		this.genreSvc = genreSvc;
	}
	
	@GetMapping("/admin/story")
	public String list(Model model, @RequestParam(defaultValue = "1") int page,
						@RequestParam(required = false) String keyword) {
		page = page < 1 ? 1 : page;
		Page<Story> stories;
		if (StringUtils.hasText(keyword)) {
			stories = storySvc.search(keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			stories = storySvc.getAll(page, Config.resultOnAdminPage);
		
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
	public String edit(Model model, @PathVariable String slug) {
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
	public String view(Model model, @PathVariable String slug) {
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
							@RequestParam(required = false) MultipartFile file,
							@RequestParam(required = false) List<Integer> genreIds,
							@Value("${upload.dir}") String uploadDir) {
		storySvc.save(story, file, genreIds, uploadDir);
		redirectAttributes.addFlashAttribute("success", "Thêm truyện thành công");
		return "redirect:/admin/story/add";
	}
	
	@PatchMapping("/admin/story/update/{slug}")
	public String update(RedirectAttributes redirectAttributes, @ModelAttribute Story story,
							@RequestParam(required = false) MultipartFile file,
							@RequestParam(required = false) List<Integer> genreIds,
							@PathVariable String slug,
							@Value("${upload.dir}") String uploadDir) {
		story = storySvc.update(slug, story, file, genreIds, uploadDir);
		redirectAttributes.addFlashAttribute("success", "Chỉnh sửa truyện thành công");
		return "redirect:/admin/story/edit/" + story.getSlug();
	}
	
	@DeleteMapping("/admin/story/delete/{slug}")
	public String delete(HttpServletRequest req, RedirectAttributes redirectAttributes, @PathVariable String slug,
							@Value("${upload.dir}") String uploadDir) {
		storySvc.delete(slug, uploadDir);
		redirectAttributes.addFlashAttribute("success", "Xóa truyện thành công");
	    return "redirect:" + req.getHeader("Referer");
	}
	
}
