package com.hsnhaan.lithub.service.Implement;

import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.AdminDAO;
import com.hsnhaan.lithub.model.Admin;
import com.hsnhaan.lithub.service.IAdminService;
import com.hsnhaan.lithub.util.StringHelper;

@Service
public class AdminServiceImpl implements IAdminService {

	private final AdminDAO adminDAO;
	private final PasswordEncoder passwordEncoder;
	
	public AdminServiceImpl(AdminDAO adminDAO, PasswordEncoder passwordEncoder) {
		this.adminDAO = adminDAO;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public Page<Admin> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return adminDAO.findAll(pageable);
	}
	
	@Override
	public Optional<Admin> getByUsername(String username) {
		return adminDAO.findByUsername(username);
	}
	
	@Override
	public Page<Admin> search(String keyword, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return adminDAO.search(keyword, pageable);
	}

	@Override
	public void save(Admin admin, String rePassword) {
		admin.setRole("ADMIN");
		admin.setFull_name(StringHelper.toTitleCase(admin.getFull_name()));
		validate(admin, rePassword, true);
		adminDAO.save(admin);
	}
	
	@Override
	public void update(String username, String full_name) {
		Admin admin = adminDAO.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		admin.setFull_name(StringHelper.toTitleCase(full_name));
		validate(admin, "", false);
		adminDAO.save(admin);
	}

	@Override
	public void delete(String username) {
		Admin admin = adminDAO.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adminDAO.delete(admin);
	}
	
	@Override
	public void changePassword(String username, String newPassword) {
		Admin admin = adminDAO.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		admin.setPassword(passwordEncoder.encode(newPassword));
		adminDAO.save(admin);
	}
	
	public void validate(Admin admin, String rePassword, boolean isNew) {
		if (isNew && !StringUtils.hasText(admin.getUsername()) || admin.getUsername().length() > 255)
			throw new IllegalArgumentException("Tên tài khoản không hợp lệ");
		if (isNew && adminDAO.existsByUsername(admin.getUsername()))
			throw new DuplicateKeyException("Tên tài khoản đã tồn tại");
		if (!StringUtils.hasText(admin.getFull_name()) || admin.getFull_name().length() > 255)
			throw new IllegalArgumentException("Họ tên không hợp lệ");
		if (isNew && !StringUtils.hasText(admin.getPassword()))
			throw new IllegalArgumentException("Mật khẩu không được để trống");
		if (isNew && !admin.getPassword().equals(rePassword))
			throw new IllegalArgumentException("Xác nhận mật khẩu không trùng khớp");
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
	}

}
