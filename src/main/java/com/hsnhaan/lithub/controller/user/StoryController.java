package com.hsnhaan.lithub.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Chapter;
import com.hsnhaan.lithub.model.Comment;
import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.security.CustomUserDetails;
import com.hsnhaan.lithub.service.Implement.ChapterServiceImpl;
import com.hsnhaan.lithub.service.Implement.CommentServiceImpl;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.RatingServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

@Controller("UserStoryController")
public class StoryController {

	private final GenreServiceImpl genreSvc;
	private final StoryServiceImpl storySvc;
	private final ChapterServiceImpl chapterSvc;
	private final CommentServiceImpl commentSvc;
	private final RatingServiceImpl ratingSvc;
	
	public StoryController(GenreServiceImpl genreSvc, StoryServiceImpl storySvc, 
			ChapterServiceImpl chapterSvc, CommentServiceImpl commentSvc,
			RatingServiceImpl ratingSvc) {
		this.genreSvc = genreSvc;
		this.storySvc = storySvc;
		this.chapterSvc = chapterSvc;
		this.commentSvc = commentSvc;
		this.ratingSvc = ratingSvc;
	}
	
	@GetMapping("/{slug}")
	public String story(Model model, @PathVariable String slug, 
			@RequestParam(defaultValue = "1") int page) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		List<Genre> genres = genreSvc.getAll();
		page = page < 1 ? 1 : page;
		Page<Chapter> chapters = chapterSvc.getByStoryId(story.getId(), page, Config.chaptersOnPage);
		Page<Comment> comments = commentSvc.getByStoryId(story.getId(), page, 1000);
		model.addAllAttributes(Map.of(
			"genres", genres,
			"story", story,
			"comments", comments,
			"chapters", chapters,
			"totalPage", chapters.getTotalPages(),
			"currentPage", page
		));
		return "user/story";
	}
	
	@GetMapping("/{slug}/{number}")
	public String chapter(Model model, @PathVariable String slug, @PathVariable int number) {
		Story story = storySvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Chapter chapter = chapterSvc.getByNumber(number, story.getId())
							.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		List<Genre> genres = genreSvc.getAll();
		model.addAllAttributes(Map.of(
			"genres", genres,
			"story", story,
			"chapter", chapter
		));
		return "user/chapter";
	}
	
	@PostMapping("/{slug}/comment")
	public String createComment(RedirectAttributes redirectAttributes, Authentication authentication,
			@PathVariable String slug, @RequestParam String content, @RequestParam int rating) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		commentSvc.save(content, slug, userDetails.getId());
		System.out.println("tahn cong");
		ratingSvc.save(rating, userDetails.getId(), slug);
		redirectAttributes.addFlashAttribute("success", "Thêm bình luận thành công");
		return "redirect:/" + slug;
	}
	
}
