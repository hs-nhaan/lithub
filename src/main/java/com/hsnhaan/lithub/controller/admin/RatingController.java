package com.hsnhaan.lithub.controller.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Rating;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.RatingServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RatingController {
	
	private final StoryServiceImpl storySvc;
	private final RatingServiceImpl ratingSvc;
	
	public RatingController(RatingServiceImpl ratingSvc, StoryServiceImpl storySvc) {
		this.ratingSvc = ratingSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping("/admin/rating/{slug}")
	public String list(Model model, @PathVariable String slug, 
						@RequestParam(defaultValue = "1") int page, 
						@RequestParam(required = false) String keyword) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		page = page < 1 ? 1 : page;
		Page<Rating> ratings;
		if (StringUtils.hasText(keyword)) {
			ratings = ratingSvc.search(story.getId(), keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			ratings = ratingSvc.getByStoryId(story.getId(), page, Config.resultOnAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách đánh giá",
			"contentPage", "admin/rating/list",
			"ratings", ratings,
			"totalPage", ratings.getTotalPages(),
			"story", story,
			"currentPage", page
		));
		
		return "admin/index";
	}
	
	@DeleteMapping("/admin/rating/delete/{id}")
	public String delete(RedirectAttributes redirectAttributes, 
			HttpServletRequest req, @PathVariable int id) {
		ratingSvc.delete(id);
		redirectAttributes.addFlashAttribute("success", "Xóa đánh giá thành công");
	    return "redirect:" + req.getHeader("Referer");
	}
	
}
