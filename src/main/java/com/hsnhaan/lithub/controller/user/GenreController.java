package com.hsnhaan.lithub.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.StoryServiceImpl;
import com.hsnhaan.lithub.util.Config;

@Controller("UserGenreController")
public class GenreController {

	private final GenreServiceImpl genreSvc;
	private final StoryServiceImpl storySvc;
	
	public GenreController(GenreServiceImpl genreSvc, StoryServiceImpl storySvc) {
		this.genreSvc = genreSvc;
		this.storySvc = storySvc;
	}
	
	@GetMapping("/the-loai/{slug}")
	public String genre(Model model, @PathVariable String slug, 
			@RequestParam(defaultValue = "1") int page) {
		Genre genre = genreSvc.getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		page = page < 1 ? 1 : page;
		List<Genre> genres = genreSvc.getAll();
		Page<Story> stories = storySvc.getByGenreId(genre.getId(), page, Config.resultOnUserPage);
		model.addAllAttributes(Map.of(
			"genres", genres,
			"genre", genre,
			"stories", stories,
			"totalPage", stories.getTotalPages(),
			"currentPage", page
		));
		return "user/genre";
	}
	
}
