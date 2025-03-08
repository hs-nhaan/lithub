package com.hsnhaan.lithub.controller.admin;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Chapter;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.ChapterServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;
import com.hsnhaan.lithub.util.StringHelper;

@Controller
public class ChapterController {

	private final ChapterServiceImpl chapterSvc;
	private final StoryServiceImpl storySvc;
	
	public ChapterController(ChapterServiceImpl chapterSvc, StoryServiceImpl storySvc) {
		this.chapterSvc = chapterSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping("/admin/chapter/{slug}")
	public String list(Model model, @PathVariable("slug") String slug, @RequestParam(name = "page", defaultValue = "1") int page,
						@RequestParam(name = "keyword", required = false) String keyword) {
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
	public String add(Model model, @PathVariable("slug") String slug) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		model.addAllAttributes(Map.of(
			"titlePage", "Thêm chương mới",
			"contentPage", "admin/chapter/add",
			"story", story
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/chapter/{slug}/edit/{number}")
	public String edit(Model model, @PathVariable("slug") String slug, @PathVariable("number") int number) {
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
	public String view(Model model, @PathVariable("slug") String slug, @PathVariable("number") int number) {
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
							@PathVariable("slug") String slug) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			chapter.setTitle(StringHelper.toTitleCase(chapter.getTitle()));
			chapter.setChapter_number(chapterSvc.nextChapter(story.getId()));
			chapter.setCreated_at(Instant.now());
			chapter.setStory(story);
			chapterSvc.save(chapter);
			redirectAttributes.addFlashAttribute("success", "Thêm chương thành công");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/chapter/" + story.getSlug() + "/add";
	}
	
	@PostMapping("/admin/chapter/update/{id}")
	public String update(RedirectAttributes redirectAttributes, @PathVariable("id") int id,
							@ModelAttribute Chapter chapter) {
		Chapter oldChapter = Optional.ofNullable(chapterSvc.getById(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			oldChapter.setTitle(StringHelper.toTitleCase(chapter.getTitle()));
			oldChapter.setContent(chapter.getContent());
			chapterSvc.update(oldChapter);
			redirectAttributes.addFlashAttribute("success", "Chỉnh sửa chương thành công");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/chapter/" + oldChapter.getStory().getSlug()+ "/edit/" + oldChapter.getChapter_number();
	}
	
	@GetMapping("/admin/chapter/delete/{id}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("id") int id) {
		Chapter chapter = Optional.ofNullable(chapterSvc.getById(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			chapterSvc.delete(chapter);
			redirectAttributes.addFlashAttribute("success", "Xóa chương truyện thành công");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/chapter/" + chapter.getStory().getSlug();
	}
	
}
