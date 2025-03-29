package com.hsnhaan.lithub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.User;

public interface IUserService {

	List<User> getAll();
	User getById(int id);
	Optional<User> getByEmail(String email);
	Page<User> getAll(int page, int limit);
	Page<User> search(String keyword, int page, int limit);
	void save(String email, String username, String password, String rePassword);
	void changePassword(String email, String password);
	void delete(int id);
	boolean existsById(int id);
	void verify(String token);
	
}
