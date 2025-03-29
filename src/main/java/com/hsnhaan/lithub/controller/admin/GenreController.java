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

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.servlet.http.HttpServletRequest;

@Validated
@Controller
public class GenreController {

	private final GenreServiceImpl genreSvc;
	
	public GenreController(GenreServiceImpl genreSvc) {
		this.genreSvc = genreSvc;
	}
	
	@GetMapping("/admin/genre")
	public String list(Model model, @RequestParam(required = false) String keyword,
						@RequestParam(defaultValue = "1") int page) {
		page = page < 1 ? 1 : page;
		Page<Genre> genres;
		if (StringUtils.hasText(keyword)) {
			genres = genreSvc.search(keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		}
		else
			genres = genreSvc.getAll(page, Config.resultOnAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage" , "Danh sách thể loại",
			"contentPage", "admin/genre/list",
			"totalPage", genres.getTotalPages(),
			"currentPage", page,
			"genres", genres
		));

		return "admin/index";
	}
	
	@GetMapping("/admin/genre/add")
	public String add(Model model) {
		model.addAllAttributes(Map.of(
			"titlePage" , "Thêm thể loại",
			"contentPage", "admin/genre/add"
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/genre/edit/{slug}")
	public String edit(Model model, @PathVariable String slug) {
		Genre genre = genreSvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAllAttributes(Map.of(
			"titlePage", genre.getName(),
			"contentPage", "admin/genre/edit",
			"genre", genre
		));
		
		return "admin/index";
	}
	
	@PostMapping("/admin/genre/create")
	public String create(RedirectAttributes redirectAttributes, @ModelAttribute Genre genre) {
		genreSvc.save(genre);
		redirectAttributes.addFlashAttribute("success", "Thêm thể loại thành công");
		return "redirect:/admin/genre/add";
	}
	
	@PatchMapping("/admin/genre/update/{slug}")
	public String update(RedirectAttributes redirectAttributes, @ModelAttribute Genre genre,
							@PathVariable String slug) {
		genre = genreSvc.update(slug, genre.getName());
		redirectAttributes.addFlashAttribute("success", "Chỉnh sửa thể loại thành công");
		return "redirect:/admin/genre/edit/" + genre.getSlug();
	}
	
	@DeleteMapping("/admin/genre/delete/{slug}")
	public String delete(RedirectAttributes redirectAttributes, HttpServletRequest req, @PathVariable String slug) {
		genreSvc.delete(slug);
		redirectAttributes.addFlashAttribute("success", "Xóa thể loại thành công");
	    return "redirect:" + req.getHeader("Referer");
	}
	
}
