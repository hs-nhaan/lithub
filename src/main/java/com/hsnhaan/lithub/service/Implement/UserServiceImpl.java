package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.UserDAO;
import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	private final UserDAO userDAO;
	private final PasswordEncoder passwordEncoder;
	private final EmailServiceImpl emailSvc;
	
	public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder,
			EmailServiceImpl emailSvc) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
		this.emailSvc = emailSvc;
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
		return userDAO.findByEmail(email);
	}

	@Override
	public Page<User> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return userDAO.findAll(pageable);
	}

	@Override
	public Page<User> search(String keyword, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return userDAO.search(keyword, pageable);
	}

	@Override
	public void save(String email, String username, String password, String rePassword) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setVerified(false);
		user.setUsername(username);
		validate(user, true, rePassword);
		String token = UUID.randomUUID().toString();
		user.setVerification_token(token);
		userDAO.save(user);
		emailSvc.sendVerificationEmail(user.getEmail(), token);
	}

	@Override
	public void changePassword(String email, String password) {
		User user = userDAO.findByEmail(email)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		user.setPassword(passwordEncoder.encode(password));
		userDAO.save(user);
	}

	@Override
	public void delete(int id) {
		User user = userDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		userDAO.delete(user);
	}

	@Override
	public boolean existsById(int id) {
		return userDAO.existsById(null);
	}
	
	@Override
	public void verify(String token) {
		User user = userDAO.findByVerification_token(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (user.getVerification_token().equals(token)) {
			user.setVerification_token("");
			user.setVerified(true);
			userDAO.save(user);
		} else
			throw new IllegalArgumentException("Mã xác nhận không hợp lệ");
	}
	
	private void validate(User user, boolean isNew, String rePassword) {
		if (!StringUtils.hasText(user.getUsername()))
			throw new IllegalArgumentException("Tên không được để trống");
		if (!user.getPassword().equals(rePassword))
			throw new IllegalArgumentException("Xác nhận mật khẩu không trùng khớp");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (isNew || !userDAO.findById(user.getId()).map(u -> u.getEmail().equals(user.getEmail())).orElse(false))
			if (userDAO.existsByEmail(user.getEmail()))
				throw new DuplicateKeyException("Email đã tồn tại");
	}
	
}
