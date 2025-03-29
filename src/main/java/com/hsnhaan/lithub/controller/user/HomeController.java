package com.hsnhaan.lithub.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;

@Controller
public class HomeController {

	private final GenreServiceImpl genreSvc;
	private final StoryServiceImpl storySvc;
	
	public HomeController(GenreServiceImpl genreSvc, StoryServiceImpl storySvc) {
		this.genreSvc = genreSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping({"/trang-chu", "/"})
	public String home(Model model) {
		List<Genre> genres = genreSvc.getAll();
		Page<Story> newStories = storySvc.getNewStory(1, 15);
		Page<Story> completedStories = storySvc.getByStatusTrue(1, 15);
		model.addAllAttributes(Map.of(
			"genres", genres,
			"newStories", newStories,
			"completedStories", completedStories
		));
		return "user/index";
	}
	
}
