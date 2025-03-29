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

import com.hsnhaan.lithub.model.Comment;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.CommentServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CommentController {

	private final CommentServiceImpl commentSvc;
	private final StoryServiceImpl storySvc;
	
	public CommentController(CommentServiceImpl commentSvc, StoryServiceImpl storySvc) {
		this.commentSvc = commentSvc;
		this.storySvc = storySvc;
	}

	@GetMapping("/admin/comment/{slug}")
	public String list(Model model, @PathVariable String slug, 
			@RequestParam(required = false) String keyword, 
			@RequestParam(defaultValue = "1") int page) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		page = page < 1 ? 1 : page;
		Page<Comment> comments;
		
		if (StringUtils.hasText(keyword)) {
			comments = commentSvc.search(story.getId(), keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			comments = commentSvc.getByStoryId(story.getId(), page, Config.resultOnAdminPage);
		
		model.addAllAttributes(Map.of(
				"titlePage", "Danh sách bình luận",
				"contentPage", "admin/comment/list",
				"totalPage", comments.getTotalPages(),
				"currentPage", page,
				"story", story,
				"comments", comments
		));
		
		return "admin/index";
	}
	
	@DeleteMapping("/admin/comment/delete{id}")
	public String delete(HttpServletRequest req, 
			RedirectAttributes redirectAttributes, @PathVariable int id) {
		commentSvc.delete(id);
		redirectAttributes.addFlashAttribute("success", "Xóa truyện thành công");
	    return "redirect:" + req.getHeader("Referer");
	}
	
}
