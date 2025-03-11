package com.hsnhaan.lithub.controller.admin;

import java.util.Map;

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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Chapter;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.ChapterServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.validation.constraints.Min;

@Validated
@Controller
public class ChapterController {

	private final ChapterServiceImpl chapterSvc;
	private final StoryServiceImpl storySvc;
	
	public ChapterController(ChapterServiceImpl chapterSvc, StoryServiceImpl storySvc) {
		this.chapterSvc = chapterSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping("/admin/chapter/{slug}")
	public String list(Model model, @PathVariable String slug, @RequestParam(defaultValue = "1") @Min(1) int page,
						@RequestParam(required = false) String keyword) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Page<Chapter> chapters = null;
		
		if (StringUtils.hasText(keyword)) {
			chapters = chapterSvc.search(keyword, story.getId(), page, Config.resultPerAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			chapters = chapterSvc.getByStoryId(story.getId(), page, Config.resultPerAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách chương truyện",
			"contentPage", "admin/chapter/list",
			"story", story,
			"currentPage", page,
			"totalPage", chapters.getTotalPages(),
			"chapters", chapters
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/chapter/{slug}/add")
	public String add(Model model, @PathVariable String slug) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		model.addAllAttributes(Map.of(
			"titlePage", "Thêm chương mới",
			"contentPage", "admin/chapter/add",
			"story", story
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/chapter/{slug}/edit/{number}")
	public String edit(Model model, @PathVariable String slug, @PathVariable int number) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Chapter chapter = chapterSvc.getByNumber(number, story.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		model.addAllAttributes(Map.of(
			"titlePage", "Chương " + chapter.getChapter_number() + " " + chapter.getTitle(),
			"contentPage", "admin/chapter/edit",
			"chapter", chapter
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/chapter/{slug}/{number}")
	public String view(Model model, @PathVariable String slug, @PathVariable int number) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Chapter chapter = chapterSvc.getByNumber(number, story.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		model.addAllAttributes(Map.of(
			"titlePage", "Chương " + chapter.getChapter_number() + " " + chapter.getTitle(),
			"contentPage", "admin/chapter/edit",
			"chapter", chapter,
			"readOnly", true
		));
		
		return "admin/index";
	}
	
	@PostMapping("/admin/chapter/{slug}/create")
	public String create(RedirectAttributes redirectAttributes, @ModelAttribute Chapter chapter,
							@PathVariable String slug) {
		try {
			
			chapterSvc.save(slug, chapter);
			redirectAttributes.addFlashAttribute("success", "Thêm chương thành công");
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/chapter/" + slug + "/add";
	}
	
	@PatchMapping("/admin/chapter/update/{id}")
	public String update(RedirectAttributes redirectAttributes, @PathVariable int id,
							@ModelAttribute Chapter chapter) {
		try {
			chapter = chapterSvc.update(id, chapter);
			redirectAttributes.addFlashAttribute("success", "Chỉnh sửa chương thành công");
			return "redirect:/admin/chapter/" + chapter.getStory().getSlug() + "/edit/" + chapter.getChapter_number();
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		chapter = chapterSvc.getById(id);
		return "redirect:/admin/chapter/" + chapter.getStory().getSlug() + "/edit/" + chapter.getChapter_number();
	}
	
	@DeleteMapping("/admin/chapter/delete/{id}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable int id) {
		Chapter chapter = chapterSvc.getById(id);
		try {
			chapterSvc.delete(id);
			redirectAttributes.addFlashAttribute("success", "Xóa chương truyện thành công");
		} catch (ResponseStatusException ex) {
			throw ex; 
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		return "redirect:/admin/chapter/" + chapter.getStory().getSlug();
	}
	
}
