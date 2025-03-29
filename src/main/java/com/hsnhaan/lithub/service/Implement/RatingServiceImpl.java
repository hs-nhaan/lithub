package com.hsnhaan.lithub.service.Implement;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.RatingDAO;
import com.hsnhaan.lithub.model.Rating;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.IRatingService;

@Service
public class RatingServiceImpl implements IRatingService {

	private final RatingDAO ratingDAO;
	private final UserServiceImpl userSvc;
	private final StoryServiceImpl storySvc;
	
	public RatingServiceImpl(RatingDAO ratingDAO, UserServiceImpl userSvc, StoryServiceImpl storySvc) {
		this.ratingDAO = ratingDAO;
		this.userSvc = userSvc;
		this.storySvc = storySvc;
	}
	
	@Override
	public Page<Rating> getByStoryId(int storyId, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return ratingDAO.findByStoryId(storyId, pageable);
	}

	@Override
	public Page<Rating> search(int storyId, String keyword, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return ratingDAO.search(storyId, keyword, pageable);
	}

	@Override
	public void save(int rating_number, int userId, String storySlug) {
		Story story = storySvc.getBySlug(storySlug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		User user = Optional.of(userSvc.getById(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Rating rating = new Rating();
		rating.setRating(rating_number);
		rating.setStory(story);
		rating.setUser(user);
		validate(rating);
		ratingDAO.save(rating);
	}

	@Override
	public void delete(int id) {
		Rating rating = ratingDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		ratingDAO.delete(rating);
	}
	
	private void validate(Rating rating) {
		if (rating.getRating() < 1 || rating.getRating() > 10)
			throw new IllegalArgumentException("Số điểm không hợp lệ");
	}
	
}
