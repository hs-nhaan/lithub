package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.CommentDAO;
import com.hsnhaan.lithub.model.Comment;
import com.hsnhaan.lithub.model.Story;
import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.ICommentService;

@Service
public class CommentServiceImpl implements ICommentService {

	private final CommentDAO commentDAO;
	private final UserServiceImpl userSvc;
	private final StoryServiceImpl storySvc;
	
	public CommentServiceImpl(CommentDAO commentDAO, UserServiceImpl userSvc, StoryServiceImpl storySvc) {
		this.commentDAO = commentDAO;
		this.userSvc = userSvc;
		this.storySvc = storySvc;
	}
	
	@Override
	public List<Comment> getAll() {
		return commentDAO.findAll();
	}

	@Override
	public Comment getById(int id) {
		return commentDAO.findById(id).orElse(null);
	}

	@Override
	public Page<Comment> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return commentDAO.findAll(pageable);
	}
	
	@Override
	public Page<Comment> getByStoryId(int storyId, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return commentDAO.findByStoryId(storyId, pageable);
	}

	@Override
	public Page<Comment> search(int storyId, String keyword, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return commentDAO.search(storyId, keyword, pageable);
	}

	@Override
	public void save(String content, String storySlug, int userId) {
		User user = Optional.ofNullable(userSvc.getById(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Story story = storySvc.getBySlug(storySlug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setStory(story);
		comment.setUser(user);
		validate(comment);
		commentDAO.save(comment);
	}

	@Override
	public void delete(int id) {
		Comment comment = commentDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		commentDAO.delete(comment);
	}
	
	private void validate(Comment comment) {
		if (!StringUtils.hasText(comment.getContent()))
			throw new IllegalArgumentException("Nội dung bình luận không được để trống");
		if (commentDAO.existsByStoryIdAndUserId(comment.getStory().getId(), comment.getUser().getId()))
			throw new DuplicateKeyException("Không thể bình luận một truyện hai lần");
	}

}
