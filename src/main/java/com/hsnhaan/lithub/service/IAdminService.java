package com.hsnhaan.lithub.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.Admin;

public interface IAdminService {

	Page<Admin> getAll(int page, int limit);
	Optional<Admin> getByUsername(String username);
	Page<Admin> search(String keyword, int page, int limit);
	void save(Admin admin, String rePassword);
	void update(String username, String full_name);
	void delete(String username);
	void changePassword(String username, String newPassword);
	
}
