package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hsnhaan.lithub.dao.UserDAO;
import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	private final UserDAO userDAO;
	
	@Autowired
	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public List<User> getAll() {
		return userDAO.findAll();
	}

	@Override
	public User getById(int id) {
		return userDAO.findById(id).orElse(null);
	}

	@Override
	public Optional<User> getByEmail(String email) {
		return Optional.ofNullable(userDAO.findByEmail(email));
	}

	@Override
	public List<User> search(String keyword) {
		return userDAO.search(keyword);
	}

	@Override
	public Page<User> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return userDAO.findAll(pageable);
	}

	@Override
	public Page<User> search(String keyword, int page, int limit) {
		List<User> users = search(keyword);
		
		Pageable pageable = PageRequest.of(page - 1, limit);
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), users.size());
		
		List<User> subUsers = users.subList(start, end);
		
		return new PageImpl<User>(subUsers, pageable, users.size());
	}

	@Override
	public void save(User user) {
		validate(user, true);
		userDAO.save(user);
	}

	@Override
	public void update(User user) {
		if (!userDAO.existsById(user.getId()))
			throw new RuntimeException("Không tìm thấy người dùng");
		validate(user, false);
		userDAO.save(user);
	}

	@Override
	public void delete(User user) {
		if (!userDAO.existsById(user.getId()))
			throw new RuntimeException("Không tìm thấy người dùng");
		userDAO.delete(user);
	}

	@Override
	public boolean existsById(int id) {
		return userDAO.existsById(null);
	}
	
	private void validate(User user, boolean isNew) {
		if (!StringUtils.hasText(user.getUsername()))
			throw new RuntimeException("Tên không được để trống");
		if (isNew || !userDAO.findById(user.getId()).map(u -> u.getEmail().equals(user.getEmail())).orElse(false))
			if (userDAO.existsByEmail(user.getEmail()))
				throw new RuntimeException("Email đã tồn tại");
	}
	
}
