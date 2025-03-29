package com.hsnhaan.lithub.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

@Controller
public class SearchController {

	private final GenreServiceImpl genreSvc;
	private final StoryServiceImpl storySvc;
	
	public SearchController(GenreServiceImpl genreSvc, StoryServiceImpl storySvc) {
		this.genreSvc = genreSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping("/tim-kiem")
	public String search(Model model, @RequestParam(required = false) String keyword, 
			@RequestParam(defaultValue = "1") int page) {
		if (keyword == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		List<Genre> genres = genreSvc.getAll();
		Page<Story> stories;
		if (StringUtils.hasText(keyword)) {
			stories = storySvc.search(keyword, page, Config.resultOnUserPage);
			model.addAttribute("keyword", keyword);
		} else
			stories = storySvc.getAll(page, Config.resultOnUserPage);
		model.addAllAttributes(Map.of(
			"genres", genres,
			"stories", stories,
			"totalPage", stories.getTotalPages(),
			"currentPage", page
		));
		return "user/search";
	}
	
}
